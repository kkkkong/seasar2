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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

import org.seasar.framework.util.EnumerationAdapter;

/**
 * {@link PortletPreferences}のモック用の実装クラスです。
 * 
 * @author shinsuke
 * 
 */
public class MockPortletPreferencesImpl implements PortletPreferences {

    private Map preferences = new HashMap();

    private Map readOnlyPreferences = new HashMap();

    public boolean isReadOnly(String key) {
        return readOnlyPreferences.containsKey(key);
    }

    public String getValue(String key, String def) {
        String[] values = (String[]) preferences.get(key);
        if (values == null || values.length == 0) {
            return def;
        }
        return values[0];
    }

    public String[] getValues(String key, String[] def) {
        String[] values = (String[]) preferences.get(key);
        if (values == null) {
            return def;
        }
        return values;
    }

    public void setValue(String key, String value) throws ReadOnlyException {
        if (isReadOnly(key)) {
            throw new ReadOnlyException(
                    "Preference " + key + " is read-only");
        }
        preferences.put(key, new String[] { value });
    }

    public void setValues(String key, String[] values)
            throws ReadOnlyException {
        if (isReadOnly(key)) {
            throw new ReadOnlyException(
                    "Preference " + key + " is read-only");
        }
        preferences.put(key, values);
    }

    public Enumeration getNames() {
        return new EnumerationAdapter(preferences.keySet().iterator());
    }

    public Map getMap() {
        return preferences;
    }

    public void reset(String key) throws ReadOnlyException {
        if (isReadOnly(key)) {
            throw new ReadOnlyException(
                    "Preference " + key + " is read-only");
        }
        preferences.remove(key);
    }

    public void store() throws IOException, ValidatorException {
    }

    /**
     * プリファレンスを設定します。
     * 
     * @param key
     * @param values
     */
    public void setPreference(String key, String[] values) {
        preferences.put(key, values);
    }

    /**
     * 読み取り専用のプリファレンスを設定します。
     * 
     * @param key
     * @param values
     */
    public void setReadOnlyPreference(String key, String[] values) {
        readOnlyPreferences.put(key, values);
    }

    /**
     * 読み取り専用かどうかを設定します。
     * 
     * @param key
     * @param readOnly
     */
    public void setReadOnly(String key, boolean readOnly) {
        if (readOnly) {
            readOnlyPreferences.put(key, preferences.get(key));
        } else {
            readOnlyPreferences.remove(key);
        }
    }
}
