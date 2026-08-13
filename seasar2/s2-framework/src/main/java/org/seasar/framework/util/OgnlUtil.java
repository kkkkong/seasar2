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

import java.util.Map;

import ognl.ClassResolver;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.exception.OgnlRuntimeException;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;

/**
 * Ognl用のユーティリティクラスです。
 * 
 * @author higa
 * 
 */
public class OgnlUtil {

    private static boolean initialized = false;

    static {
        initialize();
    }

    public static synchronized void initialize() {
        if (!initialized) {
            DisposableUtil.add(new Disposable() {
                public void dispose() {
                    OgnlRuntime.clearCache();
                }
            });
            initialized = true;
        }
    }

    /**
     * インスタンスを構築します。
     */
    protected OgnlUtil() {
    }

    /**
     * 値を返します。
     * 
     * @param exp
     * @param root
     * @return 値
     * @see #getValue(Object, Map, Object, String, int)
     */
    public static Object getValue(Object exp, Object root) {
        return getValue(exp, root, null, 0);
    }

    /**
     * 値を返します。
     * 
     * @param exp
     * @param root
     * @param path
     * @param lineNumber
     * @return 値
     * @see #getValue(Object, Map, Object, String, int)
     */
    public static Object getValue(Object exp, Object root, String path,
            int lineNumber) {
        return getValue(exp, null, root, path, lineNumber);
    }

    /**
     * 値を返します。
     * 
     * @param exp
     * @param ctx
     * @param root
     * @return 値
     * @see #getValue(Object, Map, Object, String, int)
     */
    public static Object getValue(Object exp, Map ctx, Object root) {
        return getValue(exp, ctx, root, null, 0);
    }

    /**
     * 値を返します。
     * 
     * @param exp
     * @param ctx
     * @param root
     * @param path
     * @param lineNumber
     * @return 値
     * @throws OgnlRuntimeException
     *             OgnlExceptionが発生した場合
     */
    public static Object getValue(Object exp, Map ctx, Object root,
            String path, int lineNumber) throws OgnlRuntimeException {
        try {
            return Ognl.getValue(exp, createContext(ctx, root), root);
        } catch (OgnlException ex) {
            throw new OgnlRuntimeException(ex.getReason() == null ? ex : ex
                    .getReason(), path, lineNumber);
        } catch (Exception ex) {
            throw new OgnlRuntimeException(ex, path, lineNumber);
        }
    }

    /**
     * 式を解析します。
     * 
     * @param expression
     * @return 解析した結果
     * @see #parseExpression(String, String, int)
     */
    public static Object parseExpression(String expression) {
        return parseExpression(expression, null, 0);
    }

    /**
     * 式を解析します。
     * 
     * @param expression
     * @param path
     * @param lineNumber
     * @return 解析した結果
     * @throws OgnlRuntimeException
     *             OgnlExceptionが発生した場合
     */
    public static Object parseExpression(String expression, String path,
            int lineNumber) throws OgnlRuntimeException {
        try {
            return Ognl.parseExpression(expression);
        } catch (Exception ex) {
            throw new OgnlRuntimeException(ex, path, lineNumber);
        }
    }

    /**
     * Creates an OGNL context map for expression evaluation. The context is
     * always an {@link OgnlContext} restricted by
     * {@link OgnlSecurityMemberAccess} so that dangerous JDK classes (such as
     * <code>java.lang.Runtime</code>) cannot be accessed from OGNL expressions
     * in dicon files and SQL templates.
     *
     * @param ctx
     *            the existing OGNL context map, or <code>null</code>
     * @param root
     *            the root object of the OGNL expression
     * @return a restricted {@link OgnlContext}
     */
    static OgnlContext createContext(Map ctx, Object root) {
        OgnlContext newCtx;
        if (ctx instanceof OgnlContext) {
            newCtx = (OgnlContext) ctx;
        } else if (ctx != null) {
            newCtx = new OgnlContext();
            newCtx.setValues(ctx);
        } else {
            newCtx = new OgnlContext();
        }
        if (root instanceof S2Container) {
            S2Container container = (S2Container) root;
            ClassLoader classLoader = container.getClassLoader();
            if (classLoader != null) {
                newCtx.setClassResolver(new ClassResolverImpl(classLoader));
            }
        }
        newCtx.setMemberAccess(new OgnlSecurityMemberAccess());
        return newCtx;
    }

    /**
     * Adds a {@link ClassResolver} to the context if necessary.
     *
     * @param ctx
     *            the OGNL context map
     * @param root
     *            the root object of the OGNL expression
     * @return the OGNL context map
     */
    static Map addClassResolverIfNecessary(Map ctx, Object root) {
        if (root instanceof S2Container) {
            S2Container container = (S2Container) root;
            ClassLoader classLoader = container.getClassLoader();
            if (classLoader != null) {
                ClassResolverImpl classResolver = new ClassResolverImpl(
                        classLoader);
                if (ctx == null) {
                    ctx = Ognl.createDefaultContext(root, classResolver);
                } else {
                    ctx = Ognl.addDefaultContext(root, classResolver, ctx);
                }
            }
        }
        return ctx;
    }

    /**
     * ClassResolverの実装クラスです。
     * 
     */
    public static class ClassResolverImpl implements ClassResolver {
        final private ClassLoader classLoader;

        /**
         * インスタンスを作成します。
         * 
         * @param classLoader
         */
        public ClassResolverImpl(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        public Class classForName(String className, Map ctx)
                throws ClassNotFoundException {
            try {
                return classLoader.loadClass(className);
            } catch (ClassNotFoundException ex) {
                int dot = className.indexOf('.');
                if (dot < 0) {
                    return classLoader.loadClass("java.lang." + className);
                } else {
                    throw ex;
                }
            }
        }
    }
}
