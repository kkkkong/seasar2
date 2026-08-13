/*
 * Copyright 2004-2015 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.seasar.framework.container.assembler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import junit.framework.TestCase;

import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.PropertyDef;
import org.seasar.framework.container.impl.ComponentDefImpl;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.exception.SIllegalArgumentException;

/**
 * Reproduces GitHub Issue #15 (seasarorg/seasar2).
 * <p>
 * Under HOT deploy, application classes are loaded by a per-cycle
 * {@link org.seasar.framework.container.hotdeploy.HotdeployClassLoader}.
 * When a cached {@link Field} from an EARLIER loader generation is used to
 * inject a component into an instance from a NEWER loader generation,
 * {@link Field#set(Object, Object)} throws {@link IllegalArgumentException}
 * ("Can not set X field ... to ...") even though all class NAMES match.
 * {@link org.seasar.framework.util.FieldUtil#set(Field, Object, Object)}
 * wraps that into {@link SIllegalArgumentException} [ESSR0094], which
 * propagates out of
 * {@link AbstractBindingTypeDef#setValue(ComponentDef, Field, Object, Object)}.
 * <p>
 * These tests FAIL on the current unfixed code and must PASS once
 * <code>AbstractBindingTypeDef.setValue(ComponentDef, Field, Object, Object)</code>
 * is hardened to re-resolve the field by name on the runtime class when the
 * failure signature is "same class name loaded by a different ClassLoader".
 * Genuine type mismatches must still throw.
 *
 * @author TDD reproduction for Issue #15
 */
public class Issue15Test extends TestCase {

    private static final String PREFIX = Issue15Test.class.getName() + "$";

    private ClassLoader originalClassLoader;

    protected void setUp() throws Exception {
        super.setUp();
        originalClassLoader = Thread.currentThread().getContextClassLoader();
    }

    protected void tearDown() throws Exception {
        Thread.currentThread().setContextClassLoader(originalClassLoader);
        super.tearDown();
    }

    /**
     * Core reproduction: a STALE {@link Field} (obtained from loader
     * generation GEN1) is used by
     * {@link AbstractBindingTypeDef#setValue(ComponentDef, Field, Object, Object)}
     * to inject a GEN2 value into a GEN2 target instance. All class names
     * match; only the defining {@link ClassLoader} generations differ.
     * <p>
     * Current unfixed code throws {@link SIllegalArgumentException}
     * [ESSR0094] here, so this test FAILS until the fix re-resolves the field
     * by name on the runtime class of the target.
     *
     * @throws Exception
     */
    public void testSetValueWithStaleFieldAcrossLoaderGenerations()
            throws Exception {

        // GEN1: the "previous HOT deploy cycle" whose Field got cached
        ClassLoader gen1 = new ChildFirstClassLoader(originalClassLoader);
        Class actionClass1 = gen1.loadClass(Action.class.getName());
        Field staleField = actionClass1.getField("greeter");
        // sanity: the cached Field really belongs to the GEN1 loader
        assertSame(gen1, staleField.getDeclaringClass().getClassLoader());

        // GEN2: the "current HOT deploy cycle" (same parent, fresh classes)
        ClassLoader gen2 = new ChildFirstClassLoader(originalClassLoader);
        Class actionClass2 = gen2.loadClass(Action.class.getName());
        Class greeterImplClass2 = gen2.loadClass(GreeterImpl.class.getName());
        Object target = actionClass2.newInstance();
        Object value = greeterImplClass2.newInstance();

        // sanity: identical names, distinct Class objects / loaders
        assertEquals(actionClass1.getName(), actionClass2.getName());
        assertNotSame(actionClass1, actionClass2);
        assertSame(gen2, target.getClass().getClassLoader());

        ExposedBindingTypeDef bindingDef = new ExposedBindingTypeDef();
        ComponentDef componentDef = new ComponentDefImpl(actionClass2);

        // On unfixed code this call throws SIllegalArgumentException
        // [ESSR0094] from FieldUtil.set -> Field.set
        // (IllegalArgumentException: "Can not set ... field ... to ...").
        bindingDef.exposedSetValue(componentDef, staleField, target, value);

        // The fix must re-resolve "greeter" on the GEN2 Action class and
        // complete the injection.
        Field freshField = actionClass2.getField("greeter");
        assertSame(value, freshField.get(target));
    }

    /**
     * Backward-compatibility guard: a genuinely incompatible value type must
     * STILL throw {@link SIllegalArgumentException} after the fix. The stale
     * Field scenario is reproduced with a <code>String</code>-typed field,
     * but the injected value is an {@link Integer}, which can never be
     * assigned no matter how the field is re-resolved.
     *
     * @throws Exception
     */
    public void testSetValueWithGenuinelyIncompatibleTypeStillThrows()
            throws Exception {

        ClassLoader gen1 = new ChildFirstClassLoader(originalClassLoader);
        Class actionClass1 = gen1.loadClass(Action.class.getName());
        Field staleNameField = actionClass1.getField("name");
        assertSame(gen1, staleNameField.getDeclaringClass().getClassLoader());
        assertEquals(String.class, staleNameField.getType());

        ClassLoader gen2 = new ChildFirstClassLoader(originalClassLoader);
        Class actionClass2 = gen2.loadClass(Action.class.getName());
        Object target = actionClass2.newInstance();
        Object wrongValue = new Integer(1);

        ExposedBindingTypeDef bindingDef = new ExposedBindingTypeDef();
        ComponentDef componentDef = new ComponentDefImpl(actionClass2);

        try {
            bindingDef.exposedSetValue(componentDef, staleNameField, target,
                    wrongValue);
            fail("SIllegalArgumentException expected for incompatible type");
        } catch (SIllegalArgumentException expected) {
            assertEquals("ESSR0094", expected.getMessageCode());
        }
    }

    /**
     * Test-local subclass that exposes the protected
     * {@link AbstractBindingTypeDef#setValue(ComponentDef, Field, Object, Object)}
     * method, mirroring how the framework's own assemblers invoke it.
     */
    public static class ExposedBindingTypeDef extends AbstractBindingTypeDef {

        ExposedBindingTypeDef() {
            super("must");
        }

        /**
         * @param componentDef
         * @param field
         * @param component
         * @param value
         */
        public void exposedSetValue(ComponentDef componentDef, Field field,
                Object component, Object value) {
            setValue(componentDef, field, component, value);
        }

        protected void doBind(ComponentDef componentDef,
                org.seasar.framework.beans.PropertyDesc propertyDesc,
                Object component) {
        }

        protected void doBind(ComponentDef componentDef, Field field,
                Object component) {
        }

        public void bind(ComponentDef componentDef, PropertyDef propertyDef,
                org.seasar.framework.beans.PropertyDesc propertyDesc,
                Object component) {
        }

        public void bind(ComponentDef componentDef, PropertyDef propertyDef,
                Field field, Object component) {
        }
    }

    /**
     * Child-first {@link ClassLoader} that re-defines the selected fixture
     * classes (nested classes of this test) from the parent's resource
     * stream, so the same class name exists in two distinct loader
     * generations. Modeled on
     * <code>HotdeployHttpSessionTest.ChildFirstClassLoader</code>.
     */
    public static class ChildFirstClassLoader extends ClassLoader {

        /**
         * @param parent
         */
        public ChildFirstClassLoader(ClassLoader parent) {
            super(parent);
        }

        protected synchronized Class loadClass(String name, boolean resolve)
                throws ClassNotFoundException {
            if (!name.startsWith(PREFIX)) {
                return super.loadClass(name, resolve);
            }
            synchronized (this) {
                Class loaded = findLoadedClass(name);
                if (loaded != null) {
                    if (resolve) {
                        resolveClass(loaded);
                    }
                    return loaded;
                }
            }
            InputStream is = getParent().getResourceAsStream(
                    name.replace('.', '/') + ".class");
            if (is == null) {
                throw new ClassNotFoundException(name);
            }
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                int len;
                while ((len = is.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                is.close();
                byte[] bytes = baos.toByteArray();
                Class clazz = defineClass(name, bytes, 0, bytes.length);
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }

    /**
     * Fixture: property type of the injectable field. Loaded per loader
     * generation.
     */
    public static interface Greeter {
        /**
         * @return greeting
         */
        String greet();
    }

    /**
     * Fixture: the injected component implementation.
     */
    public static class GreeterImpl implements Greeter {

        public String greet() {
            return "hello";
        }
    }

    /**
     * Fixture: the injection target. Its <code>greeter</code> field is typed
     * with the same-generation {@link Greeter} interface. Public fields are
     * used so no <code>setAccessible</code> trickery is needed.
     */
    public static class Action {

        /** */
        public Greeter greeter;

        /** */
        public String name;
    }

}
