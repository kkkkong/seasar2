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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.seasar.framework.exception.ParserConfigurationRuntimeException;
import org.seasar.framework.exception.SAXRuntimeException;
import org.xml.sax.SAXException;

/**
 * {@link SAXParser}用のユーティリティクラスです。
 * 
 * @author higa
 * 
 */
public class SAXParserFactoryUtil {

    /**
     * インスタンスを構築します。
     */
    protected SAXParserFactoryUtil() {
    }

    /**
     * {@link SAXParserFactory}の新しいインスタンスを作成します。
     * 
     * @return {@link SAXParserFactory}の新しいインスタンス
     */
    public static SAXParserFactory newInstance() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            factory.setFeature(
                "http://apache.org/xml/features/disallow-doctype-decl",
                true);
        } catch (Throwable e) {
            // ignore if not supported (old parsers may throw
            // AbstractMethodError or UnsupportedOperationException)
        }
        try {
            factory.setFeature(
                "http://xml.org/sax/features/external-general-entities",
                false);
        } catch (Throwable e) {
            // ignore if not supported (old parsers may throw
            // AbstractMethodError or UnsupportedOperationException)
        }
        try {
            factory.setFeature(
                "http://xml.org/sax/features/external-parameter-entities",
                false);
        } catch (Throwable e) {
            // ignore if not supported (old parsers may throw
            // AbstractMethodError or UnsupportedOperationException)
        }
        try {
            factory.setXIncludeAware(false);
        } catch (Throwable e) {
            // ignore if not supported (old parsers may throw
            // AbstractMethodError or UnsupportedOperationException)
        }
        return factory;
    }

    /**
     * デフォルト構成の{@link SAXParserFactory}を使って{@link SAXParser}の新しいインスタンスを作成します。
     * 
     * @return {@link SAXParser}の新しいインスタンス
     * @throws SAXRuntimeException
     *             {@link SAXParser}の作成中に{@link SAXException}がスローされた場合
     */
    public static SAXParser newSAXParser() {
        return newSAXParser(newInstance());
    }

    /**
     * 指定の{@link SAXParserFactory}を使って{@link SAXParser}の新しいインスタンスを作成します。
     * 
     * @param factory
     *            {@link SAXParserFactory}
     * @return {@link SAXParser}の新しいインスタンス
     * @throws SAXRuntimeException
     *             {@link SAXParser}の作成中に{@link SAXException}がスローされた場合
     */
    public static SAXParser newSAXParser(SAXParserFactory factory) {
        try {
            return factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            throw new ParserConfigurationRuntimeException(e);
        } catch (SAXException e) {
            throw new SAXRuntimeException(e);
        }
    }

    /**
     * XIncludeの有効／無効を設定します。
     *
     * @param spf
     *            {@link SAXParserFactory}
     * @param state
     *            XIncludeを有効にするなら<code>true</code>
     * @return XIncludeの有効／無効を設定できる場合は<code>true</code>
     */
    public static boolean setXIncludeAware(final SAXParserFactory spf,
            final boolean state) {
        try {
            // Prefer the public JAXP API so that this works on JDK 9+ where the
            // JDK internal implementation class lives in a non-exported module
            // and is therefore not accessible via reflection.
            spf.setXIncludeAware(state);
            return true;
        } catch (final Throwable ignore) {
            // fall through to reflection for parsers that declare the method
            // but whose JAXP API implementation does not support XInclude.
        }
        try {
            final Method method = spf.getClass().getMethod("setXIncludeAware",
                    new Class[] { boolean.class });
            method.invoke(spf, new Object[] { Boolean.valueOf(state) });
            return true;
        } catch (final Exception ignore) {
            return false;
        }
    }

}
