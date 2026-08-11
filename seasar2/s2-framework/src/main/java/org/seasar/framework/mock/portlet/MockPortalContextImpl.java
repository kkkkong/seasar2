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
package org.seasar.framework.mock.portlet;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.seasar.framework.util.EnumerationAdapter;

/**
 * {@link PortalContext}のモック用の実装クラスです。
 * 
 * @author shinsuke
 * 
 */
public class MockPortalContextImpl implements PortalContext {

    private static final String PORTAL_INFO = "mock/1.0";

    private Map properties = new HashMap();

    private List portletModes = new ArrayList();

    private List windowStates = new ArrayList();

    /**
     * {@link MockPortalContextImpl}を作成します。
     */
    public MockPortalContextImpl() {
        portletModes.add(PortletMode.VIEW);
        portletModes.add(PortletMode.EDIT);
        portletModes.add(PortletMode.HELP);
        windowStates.add(WindowState.NORMAL);
        windowStates.add(WindowState.MAXIMIZED);
        windowStates.add(WindowState.MINIMIZED);
    }

    public String getProperty(String name) {
        return (String) properties.get(name);
    }

    public Enumeration getPropertyNames() {
        return new EnumerationAdapter(properties.keySet().iterator());
    }

    public Enumeration getSupportedPortletModes() {
        return new EnumerationAdapter(portletModes.iterator());
    }

    public Enumeration getSupportedWindowStates() {
        return new EnumerationAdapter(windowStates.iterator());
    }

    public String getPortalInfo() {
        return PORTAL_INFO;
    }

    /**
     * プロパティを設定します。
     * 
     * @param name
     * @param value
     */
    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    /**
     * サポートする{@link PortletMode}を追加します。
     * 
     * @param portletMode
     */
    public void addPortletMode(PortletMode portletMode) {
        portletModes.add(portletMode);
    }

    /**
     * サポートする{@link PortletMode}を削除します。
     * 
     * @param portletMode
     */
    public void removePortletMode(PortletMode portletMode) {
        portletModes.remove(portletMode);
    }

    /**
     * サポートする{@link WindowState}を追加します。
     * 
     * @param windowState
     */
    public void addWindowState(WindowState windowState) {
        windowStates.add(windowState);
    }

    /**
     * サポートする{@link WindowState}を削除します。
     * 
     * @param windowState
     */
    public void removeWindowState(WindowState windowState) {
        windowStates.remove(windowState);
    }
}
