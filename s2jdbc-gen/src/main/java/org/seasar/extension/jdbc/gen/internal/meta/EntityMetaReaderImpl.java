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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.Entity;

import org.seasar.extension.jdbc.EntityMeta;
import org.seasar.extension.jdbc.EntityMetaFactory;
import org.seasar.extension.jdbc.PropertyMeta;
import org.seasar.extension.jdbc.gen.internal.exception.EntityClassNotFoundRuntimeException;
import org.seasar.extension.jdbc.gen.internal.exception.SourceParsingRuntimeException;
import org.seasar.extension.jdbc.gen.internal.meta.JavadocASTReader.JavadocResult;
import org.seasar.extension.jdbc.gen.internal.util.EntityMetaUtil;
import org.seasar.extension.jdbc.gen.internal.util.PropertyMetaUtil;
import org.seasar.extension.jdbc.gen.meta.EntityMetaReader;
import org.seasar.framework.util.ClassTraversal;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.ClassTraversal.ClassHandler;

/**
 * {@link EntityMetaReader}の実装クラスです。
 * 
 * @author taedium
 */
public class EntityMetaReaderImpl implements EntityMetaReader {

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
     * javaファイルが存在するディレクトリのリスト、{@code readComment}が{@code true}の場合{@code null}
     * であってはならない
     */
    protected List<File> javaFileSrcDirList = new ArrayList<File>();

    /**
     * javaファイルのエンコーディング、{@code readComment}が{@code true}の場合{@code null}
     * であってはならない
     */
    protected String javaFileEncoding;

    /** JavadocコメントのASTリーダ */
    protected JavadocASTReader javadocAstReader = new JavadocASTReader();

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
        for (EntityMeta entityMeta : entityMetaList) {
            doReadComment(entityMeta);
        }
    }

    /**
     * 1つのエンティティメタデータに対してコメントを読み込みます。
     * 
     * @param entityMeta
     *            エンティティメタデータ
     */
    protected void doReadComment(EntityMeta entityMeta) {
        Class<?> clazz = entityMeta.getEntityClass();
        JavadocResult result = readJavadoc(clazz);
        if (result == null) {
            return;
        }
        EntityMetaUtil.setComment(entityMeta, result.classComment);

        Set<String> processedPropertyNameSet = new HashSet<String>();
        mergePropertyComments(entityMeta, result, processedPropertyNameSet);

        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null
                && !Object.class.getName().equals(superclass.getName())) {
            JavadocResult superResult = readJavadoc(superclass);
            if (superResult == null || !superResult.mappedSuperclass) {
                break;
            }
            mergePropertyComments(entityMeta, superResult,
                    processedPropertyNameSet);
            superclass = superclass.getSuperclass();
        }
    }

    /**
     * 指定されたクラスのJavaソースファイルからJavadocを読み取ります。
     * 
     * @param clazz
     *            クラス
     * @return Javadocの読み取り結果、ソースファイルが見つからない場合は{@code null}
     */
    protected JavadocResult readJavadoc(Class<?> clazz) {
        File javaFile = findJavaFile(clazz);
        if (javaFile == null) {
            return null;
        }
        try {
            return javadocAstReader.read(javaFile, javaFileEncoding);
        } catch (RuntimeException e) {
            throw new SourceParsingRuntimeException(javaFile, e);
        }
    }

    /**
     * エンティティメタデータに対するプロパティコメントを結果からマージします。
     * すでに設定されているプロパティは上書きしません。
     * 
     * @param entityMeta
     *            エンティティメタデータ
     * @param result
     *            Javadocの読み取り結果
     * @param processedPropertyNameSet
     *            処理済みプロパティ名のセット
     */
    protected void mergePropertyComments(EntityMeta entityMeta,
            JavadocResult result, Set<String> processedPropertyNameSet) {
        for (PropertyMeta propertyMeta : entityMeta.getAllPropertyMeta()) {
            String name = propertyMeta.getName();
            if (processedPropertyNameSet.contains(name)) {
                continue;
            }
            String comment = result.fieldCommentMap.get(name);
            if (comment == null) {
                continue;
            }
            PropertyMetaUtil.setComment(propertyMeta, comment);
            processedPropertyNameSet.add(name);
        }
    }

    /**
     * クラスに対応するJavaソースファイルを検索します。
     * 
     * @param clazz
     *            クラス
     * @return Javaソースファイル、見つからない場合は{@code null}
     */
    protected File findJavaFile(Class<?> clazz) {
        String className = clazz.getName();
        String relativePath = className.replace('.', File.separatorChar)
                + ".java";
        for (File srcDir : javaFileSrcDirList) {
            File file = new File(srcDir, relativePath);
            if (file.exists() && file.isFile()) {
                return file;
            }
        }
        return null;
    }

    public boolean isFiltered() {
        return !shortClassNamePattern.pattern().equals(".*")
                || !ignoreShortClassNamePattern.pattern().equals("");
    }

}
