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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.framework.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.seasar.framework.exception.OgnlRuntimeException;

/**
 * Test of the restriction applied by {@link OgnlSecurityMemberAccess}.
 */
public class OgnlSecurityMemberAccessTest extends TestCase {

    /**
     * Reading a public constant from the java.lang.reflect package must be
     * allowed (harmless introspection).
     */
    public void testAllowsPublicFieldRead() throws Exception {
        Object value = OgnlUtil.getValue(
                OgnlUtil.parseExpression("@java.lang.reflect.Modifier@PUBLIC"),
                null);
        assertEquals(new Integer(Modifier.PUBLIC), value);
    }

    /**
     * Reading a Method's public <code>name</code> property (through the
     * getName() getter) must be allowed.
     */
    public void testAllowsReflectGetterRead() throws Exception {
        Map context = new HashMap();
        context.put("method", getClass().getMethod("hoge", new Class[0]));
        Object value = OgnlUtil.getValue(
                OgnlUtil.parseExpression("#method.name"), context, null);
        assertEquals("hoge", value);
    }

    public void testBlocksRuntime() throws Exception {
        assertBlocked("@java.lang.Runtime@getRuntime()", null);
    }

    public void testBlocksClassForName() throws Exception {
        assertBlocked("@java.lang.Class@forName(\"java.lang.Runtime\")", null);
    }

    public void testBlocksSystemExit() throws Exception {
        assertBlocked("@java.lang.System@exit(0)", null);
    }

    public void testBlocksMethodInvoke() throws Exception {
        Map context = new HashMap();
        context.put("method", getClass().getMethod("hoge", new Class[0]));
        assertBlocked("#method.invoke(null)", context);
    }

    public void testBlocksSetAccessible() throws Exception {
        Map context = new HashMap();
        context.put("method", getClass().getMethod("hoge", new Class[0]));
        assertBlocked("#method.setAccessible(true)", context);
    }

    public String hoge() {
        return "hoge";
    }

    private void assertBlocked(final String expression, final Map context)
            throws Exception {
        Object exp = OgnlUtil.parseExpression(expression);
        try {
            OgnlUtil.getValue(exp, context, null);
            fail(expression + " should be blocked by OgnlSecurityMemberAccess");
        } catch (OgnlRuntimeException expected) {
        }
    }
}