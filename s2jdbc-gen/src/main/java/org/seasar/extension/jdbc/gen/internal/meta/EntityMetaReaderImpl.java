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
package org.seasar.extension.jdbc.gen.internal.meta;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.Entity;

import org.seasar.extension.jdbc.EntityMeta;
import org.seasar.extension.jdbc.EntityMetaFactory;
import org.seasar.extension.jdbc.gen.internal.exception.DocletUnavailableRuntimeException;
import org.seasar.extension.jdbc.gen.internal.exception.EntityClassNotFoundRuntimeException;
import org.seasar.extension.jdbc.gen.internal.util.FileUtil;
import org.seasar.extension.jdbc.gen.meta.EntityMetaReader;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.ClassTraversal;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.ClassTraversal.ClassHandler;

/**
 * {@link EntityMetaReader}の実装クラスです。
 * 
 * @author taedium
 */
public class EntityMetaReaderImpl implements EntityMetaReader {

    /** ロガー */
    protected static Logger logger = Logger
            .getLogger(EntityMetaReaderImpl.class);

    /** 旧{@code com.sun.tools.javadoc.Main}が使用可能な場合{@code true} */
    protected static boolean docletAvailable;
    static {
        try {
            Class.forName("com.sun.tools.javadoc.Main"); // tools.jar
            docletAvailable = true;
        } catch (final Throwable ignore) {
        }
    }

    /** ルートディレクトリ */
    protected File classpathDir;

    /** 読み取り対象とするパッケージ名 */
    protected String packageName;

    /** エンティティメタデータのファクトリ */
    protected EntityMetaFactory entityMetaFactory;

    /** 読み取り対象とするエンティティクラス名のパターン */
    protected Pattern shortClassNamePattern;

    /** 読み取り非対象とするエンティティクラス名のパターン */
    protected Pattern ignoreShortClassNamePattern;

    /** コメントを読む場合 {@code true} */
    protected boolean readComment;

    /**
     * javaファイルが存在するディレクトリのリスト、{@code useComment}が{@code true}の場合{@code null}
     * であってはならない
     */
    protected List<File> javaFileSrcDirList = new ArrayList<File>();

    /**
     * javaファイルのエンコーディング、{@code useComment}が{@code true}の場合{@code null}
     * であってはならない
     */
    protected String javaFileEncoding;

    /**
     * インタスタンスを構築します。
     * 
     * @param classpathDir
     *            ルートディレクトリ
     * @param packageName
     *            パッケージ名、パッケージ名を指定しない場合は{@code null}
     * @param entityMetaFactory
     *            エンティティメタデータのファクトリ
     * @param shortClassNamePattern
     *            対象とするエンティティクラス名の正規表現
     * @param ignoreShortClassNamePattern
     *            対象としないエンティティクラス名の正規表現
     * @param readComment
     *            エンティティのコメントを使用する場合 {@code true}
     * @param javaFileSrcDirList
     *            javaファイルが存在するディレクトリのリスト、{@code readComment}が{@code true}の場合
     *            {@code null}であってはならない
     * @param javaFileEncoding
     *            javaファイルのエンコーディング、{@code readComment}が{@code true}の場合{@code
     *            null}であってはならない
     */
    public EntityMetaReaderImpl(File classpathDir, String packageName,
            EntityMetaFactory entityMetaFactory, String shortClassNamePattern,
            String ignoreShortClassNamePattern, boolean readComment,
            List<File> javaFileSrcDirList, String javaFileEncoding) {
        if (classpathDir == null) {
            throw new NullPointerException("classpathDir");
        }
        if (entityMetaFactory == null) {
            throw new NullPointerException("entityMetaFactory");
        }
        if (shortClassNamePattern == null) {
            throw new NullPointerException("shortClassNamePattern");
        }
        if (ignoreShortClassNamePattern == null) {
            throw new NullPointerException("ignoreShortClassNamePattern");
        }
        if (readComment) {
            if (javaFileSrcDirList == null) {
                throw new NullPointerException("javaFileSrcDirList");
            }
            if (javaFileSrcDirList.isEmpty()) {
                throw new IllegalArgumentException("javaFileSrcDirList");
            }
            if (javaFileEncoding == null) {
                throw new NullPointerException("javaFileEncoding");
            }
        }
        this.classpathDir = classpathDir;
        this.packageName = packageName;
        this.entityMetaFactory = entityMetaFactory;
        this.shortClassNamePattern = Pattern.compile(shortClassNamePattern);
        this.ignoreShortClassNamePattern = Pattern
                .compile(ignoreShortClassNamePattern);
        this.readComment = readComment;
        if (javaFileSrcDirList != null) {
            this.javaFileSrcDirList.addAll(javaFileSrcDirList);
        }
        this.javaFileEncoding = javaFileEncoding;
    }

    public List<EntityMeta> read() {
        final List<EntityMeta> entityMetaList = new ArrayList<EntityMeta>();

        ClassTraversal.forEach(classpathDir, new ClassHandler() {

            public void processClass(String packageName, String shortClassName) {
                if (isTargetPackage(packageName)
                        && isTargetClass(shortClassName)) {
                    String className = ClassUtil.concatName(packageName,
                            shortClassName);
                    Class<?> clazz = ClassUtil.forName(className);
                    if (clazz.isAnnotationPresent(Entity.class)) {
                        EntityMeta entityMeta = entityMetaFactory
                                .getEntityMeta(clazz);
                        entityMetaList.add(entityMeta);
                    }
                }
            }
        });

        if (entityMetaList.isEmpty()) {
            throw new EntityClassNotFoundRuntimeException(classpathDir,
                    packageName, shortClassNamePattern.pattern(),
                    ignoreShortClassNamePattern.pattern());
        }

        if (readComment) {
            readComment(entityMetaList);
        }

        return entityMetaList;
    }

    /**
     * 読み取りの対象パッケージの場合{@code true}を返します。
     * 
     * @param packageName
     *            パッケージ名
     * @return 読み取りの対象パッケージの場合{@code true}
     */
    protected boolean isTargetPackage(String packageName) {
        if (packageName == null) {
            return true;
        }
        if (packageName.equals(this.packageName)) {
            return true;
        }
        if (packageName.startsWith(this.packageName + ".")) {
            return true;
        }
        return false;
    }

    /**
     * 読み取りの対象クラスの場合{@code true}を返します。
     * 
     * @param shortClassName
     *            クラスの単純名
     * @return 読み取りの対象クラスの場合{@code true}
     */
    protected boolean isTargetClass(String shortClassName) {
        if (!shortClassNamePattern.matcher(shortClassName).matches()) {
            return false;
        }
        if (ignoreShortClassNamePattern.matcher(shortClassName).matches()) {
            return false;
        }
        return true;
    }

    /**
     * コメントを読みコメントをメタデータに設定します。
     * 
     * @param entityMetaList
     *            エンティティメタデータのリスト
     */
    protected void readComment(List<EntityMeta> entityMetaList) {
        if (!docletAvailable) {
            // JDK 9以降では旧式のdoclet APIが削除されているため、
            // コメントの読み込みをスキップして処理を継続します。
            logger.log("DS2JDBCGen0020", new Object[] { "com.sun.tools.javadoc.Main" });
            return;
        }
        String[] args = createDocletArgs();
        StringBuilder buf = new StringBuilder();
        for (String arg : args) {
            buf.append(arg).append(" ");
        }
        logger.log("DS2JDBCGen0019", new Object[] { buf.toString() });

        CommentDocletContext.setEntityMetaList(entityMetaList);
        try {
            invokeJavadocTool(args);
        } finally {
            CommentDocletContext.setEntityMetaList(null);
        }
    }

    /**
     * 旧式の{@code com.sun.tools.javadoc.Main}をリフレクション経由で起動します。
     * <p>
     * JDK 9以降では{@code com.sun.tools.javadoc}パッケージが削除されたため、
     * ここではリフレクションによって起動可能な場合のみコメントを読み込み、
     * 起動できない場合は何もせずに復帰します（コメントは空のまま）。
     * これによりJDK 8以前のtools.jarがクラスパスにある環境では従来どおり動作し、
     * JDK 9以降でも例外にならずに処理を継続できます。
     *
     * @param args
     *            旧式Javadocツールへの引数
     */
    protected void invokeJavadocTool(String[] args) {
        try {
            Class.forName("com.sun.javadoc.RootDoc"); // check old doclet API
        } catch (final Throwable ignore) {
            return; // doclet API not available - no comments can be extracted
        }
        try {
            Class mainClass = Class.forName("com.sun.tools.javadoc.Main");
            Method execute = mainClass.getMethod("execute", new Class[] { String[].class });
            execute.invoke(null, new Object[] { args });
        } catch (Throwable ignore) {
            // tools.jar not present on JDK 9+ or reflective call failed;
            // degrade gracefully without comment
        }
    }

    /**
     * 旧式Javadocツールの引数の配列を作成します。
     *
     * @return 引数の配列
     */
    protected String[] createDocletArgs() {
        StringBuilder srcDirListBuf = new StringBuilder();
        for (File dir : javaFileSrcDirList) {
            srcDirListBuf.append(FileUtil.getCanonicalPath(dir));
            srcDirListBuf.append(File.pathSeparator);
        }
        srcDirListBuf.setLength(srcDirListBuf.length()
                - File.pathSeparator.length());

        List<String> args = new ArrayList<String>();
        args.add("-doclet");
        args.add(CommentDoclet.class.getName());
        args.add("-sourcepath");
        args.add(srcDirListBuf.toString());
        args.add("-encoding");
        args.add(javaFileEncoding);
        args.add("-subpackages");
        args.add(packageName);
        if (logger.isDebugEnabled()) {
            args.add("-verbose");
        }
        return args.toArray(new String[args.size()]);
    }

    public boolean isFiltered() {
        return !shortClassNamePattern.pattern().equals(".*")
                || !ignoreShortClassNamePattern.pattern().equals("");
    }

}
