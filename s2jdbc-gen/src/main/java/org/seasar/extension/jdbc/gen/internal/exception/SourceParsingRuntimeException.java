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
package org.seasar.extension.jdbc.gen.internal.exception;

import java.io.File;

import org.seasar.framework.exception.SRuntimeException;

/**
 * Javaソースファイルの解析に失敗した場合にスローされます。
 *
 * @author taedium
 */
public class SourceParsingRuntimeException extends SRuntimeException {

    private static final long serialVersionUID = 1L;

    /** 解析に失敗したソースファイル */
    protected File sourceFile;

    /**
     * インスタンスを構築します。
     * 
     * @param sourceFile
     *            解析に失敗したソースファイル
     * @param cause
     *            原因となった例外
     */
    public SourceParsingRuntimeException(File sourceFile, Throwable cause) {
        super("ES2JDBCGen0030", new Object[] { sourceFile.getPath() }, cause);
        this.sourceFile = sourceFile;
    }

    /**
     * 解析に失敗したソースファイルを返します。
     * 
     * @return 解析に失敗したソースファイル
     */
    public File getSourceFile() {
        return sourceFile;
    }

}
