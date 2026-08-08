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
package org.seasar.framework.container.customizer;

import java.lang.reflect.Method;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.factory.AspectDefFactory;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;

/**
 * @author koichik
 */
public class TxAttributeCustomizer extends AbstractCustomizer {

    /** トランザクション属性に対応するインターセプタ名の<code>Map</code> */
    protected static final Map<TransactionAttributeType, String> txInterceptors = CollectionsUtil
            .newHashMap();
    static {
        txInterceptors.put(TransactionAttributeType.MANDATORY,
                "j2ee.mandatoryTx");
        txInterceptors
                .put(TransactionAttributeType.REQUIRED, "j2ee.requiredTx");
        txInterceptors.put(TransactionAttributeType.REQUIRES_NEW,
                "j2ee.requiresNewTx");
        txInterceptors.put(TransactionAttributeType.NOT_SUPPORTED,
                "j2ee.notSupportedTx");
        txInterceptors.put(TransactionAttributeType.NEVER, "j2ee.neverTx");
    }

    /** デフォルトのトランザクション属性 */
    protected TransactionAttributeType defaultAttributeType = TransactionAttributeType.REQUIRED;

    /**
     * インスタンスを構築します。
     */
    public TxAttributeCustomizer() {
    }

    /**
     * デフォルトのトランザクション属性を設定します。
     * 
     * @param defaultAttributeType
     *            デフォルトのトランザクション属性
     */
    public void setDefaultAttributeType(
            final TransactionAttributeType defaultAttributeType) {
        this.defaultAttributeType = defaultAttributeType;
    }

    @Override
    protected void doCustomize(final ComponentDef componentDef) {
        final Class<?> componentClass = componentDef.getComponentClass();
        final TransactionAttribute classAttribute = componentClass
                .getAnnotation(TransactionAttribute.class);
        final TransactionAttributeType classAttributeType = classAttribute != null ? classAttribute
                .value()
                : defaultAttributeType;
        // Class.getDeclaredMethods() returns methods in JVM-dependent order
        // under -noverify (verified on JDK 8: order flips between runs), which
        // would make the aspect order non-deterministic. Read the method
        // declaration order from the class file via javassist instead.
        final javassist.ClassPool pool = org.seasar.framework.util.ClassPoolUtil
                .getClassPool(componentClass);
        final javassist.CtClass ctClass = org.seasar.framework.util.ClassPoolUtil
                .toCtClass(pool, componentClass);
        final String[] methodNames;
        synchronized (ctClass) {
            final javassist.CtMethod[] ctMethods = ctClass
                    .getDeclaredMethods();
            methodNames = new String[ctMethods.length];
            for (int i = 0; i < ctMethods.length; ++i) {
                methodNames[i] = ctMethods[i].getName();
            }
        }
        final Method[] declaredMethods = componentClass.getDeclaredMethods();
        final Method[] methods = new Method[methodNames.length];
        int index = 0;
        for (int i = 0; i < methodNames.length; ++i) {
            for (int j = 0; j < declaredMethods.length; ++j) {
                if (declaredMethods[j] != null
                        && methodNames[i].equals(declaredMethods[j].getName())) {
                    methods[index++] = declaredMethods[j];
                    declaredMethods[j] = null;
                    break;
                }
            }
        }
        for (final Method method : methods) {
            if (method.isSynthetic() || method.isBridge()) {
                continue;
            }
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }
            final TransactionAttribute methodAttribute = method
                    .getAnnotation(TransactionAttribute.class);
            final TransactionAttributeType methodAttributeType = methodAttribute != null ? methodAttribute
                    .value()
                    : classAttributeType;
            final String interceptorName = txInterceptors
                    .get(methodAttributeType);
            if (!StringUtil.isEmpty(interceptorName)) {
                componentDef.addAspectDef(AspectDefFactory.createAspectDef(
                        interceptorName, method));
            }
        }
    }

}
