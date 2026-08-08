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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;
import ognl.MemberAccess;

/**
 * Restricted OGNL MemberAccess that blocks access to dangerous JDK classes.
 */
public class OgnlSecurityMemberAccess implements MemberAccess {

    private static final String[] BLOCKED_PACKAGES = {
        "java.lang.invoke.",
        "sun.",
        "com.sun.",
    };

    private static final String[] BLOCKED_CLASSES = {
        "java.lang.Runtime",
        "java.lang.ProcessBuilder",
        "java.lang.System",
        "java.lang.ClassLoader",
        "java.lang.Class",
        "java.lang.reflect.AccessibleObject",
    };

    /**
     * Reflective member names that represent execution or write vectors. These
     * names are only blocked for members declared in the
     * <code>java.lang.reflect</code> package so that harmless introspection
     * (e.g. reading <code>Method.name</code>) keeps working.
     */
    private static final String[] BLOCKED_REFLECT_MEMBER_NAMES = {
        "invoke",
        "newInstance",
        "setAccessible",
        "set",
        "setBoolean",
        "setByte",
        "setShort",
        "setChar",
        "setInt",
        "setLong",
        "setFloat",
        "setDouble",
        "getClassLoader",
        "getMethod",
        "getDeclaredMethod",
        "getConstructor",
        "getDeclaredConstructor",
        "getField",
        "getDeclaredField",
        "forName",
    };

    public Object setup(Map context, Object target, Member member, String propertyName) {
        Object result = null;
        if (isAccessible(context, target, member, propertyName)) {
            AccessibleObject accessible = (AccessibleObject) member;
            if (!accessible.isAccessible()) {
                result = Boolean.FALSE;
                accessible.setAccessible(true);
            }
        }
        return result;
    }

    public void restore(Map context, Object target, Member member, String propertyName, Object state) {
        if (state != null) {
            AccessibleObject accessible = (AccessibleObject) member;
            accessible.setAccessible(((Boolean) state).booleanValue());
        }
    }

    public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
        int modifiers = member.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            return false;
        }

        Class declaringClass = member.getDeclaringClass();
        String className = declaringClass.getName();

        // Block dangerous classes entirely
        for (int i = 0; i < BLOCKED_CLASSES.length; i++) {
            if (className.equals(BLOCKED_CLASSES[i])) {
                return false;
            }
        }

        // Block dangerous packages
        for (int i = 0; i < BLOCKED_PACKAGES.length; i++) {
            if (className.startsWith(BLOCKED_PACKAGES[i])) {
                return false;
            }
        }

        // java.lang.reflect is now allowed for harmless introspection (e.g.
        // reading Method.name or a public constant via getters/public fields),
        // but execution/write vectors into the reflection API stay blocked.
        if (className.startsWith("java.lang.reflect.")) {
            String memberName = member.getName();
            for (int i = 0; i < BLOCKED_REFLECT_MEMBER_NAMES.length; i++) {
                if (memberName.equals(BLOCKED_REFLECT_MEMBER_NAMES[i])) {
                    return false;
                }
            }
        }

        return true;
    }
}