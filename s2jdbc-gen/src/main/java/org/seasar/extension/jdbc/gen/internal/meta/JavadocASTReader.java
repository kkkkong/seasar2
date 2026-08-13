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
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.MappedSuperclass;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithJavadoc;

/**
 * JavaParserを使用してエンティティクラスのJavadocコメントを読み取るクラスです。
 * 
 * @author taedium
 */
public class JavadocASTReader {

    /** Javadoc各行の先頭の空白とアスタリスクを取り除くパターン */
    protected static Pattern JAVADOC_LINE_PATTERN = Pattern
            .compile("^\\s*\\*\\s?(.*)$");

    /**
     * インスタンスを構築します。
     */
    protected JavadocASTReader() {
    }

    /**
     * JavaソースファイルからJavadocコメントを読み取ります。
     * 
     * @param javaFile
     *            Javaソースファイル
     * @param encoding
     *            ファイルのエンコーディング
     * @return 読み取り結果
     */
    public JavadocResult read(File javaFile, String encoding) {
        CompilationUnit unit = parse(javaFile, encoding);
        ClassOrInterfaceDeclaration node = findPrimaryTypeDeclaration(unit);
        if (node == null) {
            return new JavadocResult(null, new HashMap<String, String>(),
                    false);
        }

        String classComment = extractComment(node);
        Map<String, String> fieldCommentMap = extractFieldCommentMap(node);
        boolean mappedSuperclass = hasMappedSuperclassAnnotation(node);
        return new JavadocResult(classComment, fieldCommentMap,
                mappedSuperclass);
    }

    /**
     * ファイルをパースします。
     * 
     * @param javaFile
     *            Javaソースファイル
     * @param encoding
     *            ファイルのエンコーディング
     * @return コンピレーション単位
     */
    protected CompilationUnit parse(File javaFile, String encoding) {
        try {
            return StaticJavaParser.parse(javaFile,
                    Charset.forName(encoding));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * コンピレーション単位から主となる公開型宣言を返します。
     * 
     * @param unit
     *            コンピレーション単位
     * @return 主となる公開型宣言、見つからない場合は{@code null}
     */
    protected ClassOrInterfaceDeclaration findPrimaryTypeDeclaration(
            CompilationUnit unit) {
        com.github.javaparser.ast.NodeList<TypeDeclaration<?>> types = unit
                .getTypes();
        if (types == null) {
            return null;
        }
        for (TypeDeclaration<?> type : types) {
            if (type instanceof ClassOrInterfaceDeclaration) {
                return (ClassOrInterfaceDeclaration) type;
            }
        }
        return null;
    }

    /**
     * 型宣言からJavadocコメントのテキストを抽出します。
     * 
     * @param node
     *            型宣言
     * @return Javadocコメントのテキスト、存在しない場合は{@code null}
     */
    protected String extractComment(
            ClassOrInterfaceDeclaration node) {
        return cleanJavadocContent(node.getJavadocComment().orElse(null));
    }

    /**
     * 型宣言からフィールド名をキー、Javadocコメントを値とするマップを作成します。
     * 
     * @param node
     *            型宣言
     * @return フィールド名をキー、Javadocコメントを値とするマップ
     */
    protected Map<String, String> extractFieldCommentMap(
            ClassOrInterfaceDeclaration node) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        com.github.javaparser.ast.NodeList<BodyDeclaration<?>> members = node
                .getMembers();
        for (BodyDeclaration<?> member : members) {
            if (!(member instanceof FieldDeclaration)) {
                continue;
            }
            FieldDeclaration field = (FieldDeclaration) member;
            String comment = cleanJavadocContent(field.getJavadocComment()
                    .orElse(null));
            if (comment == null) {
                continue;
            }
            List<VariableDeclarator> variables = field.getVariables();
            for (VariableDeclarator variable : variables) {
                map.put(variable.getName().asString(), comment);
            }
        }
        return map;
    }

    /**
     * Javadocコメントの内容を整形します。
     * 
     * @param javadocComment
     *            Javadocコメント
     * @return 整形されたコメント、{@code null}の場合は{@code null}
     */
    protected String cleanJavadocContent(
            com.github.javaparser.ast.comments.JavadocComment javadocComment) {
        if (javadocComment == null) {
            return null;
        }
        String content = javadocComment.getContent();
        if (content == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        String[] lines = content.split("\\r\\n|\\r|\\n");
        boolean appended = false;
        for (String line : lines) {
            Matcher matcher = JAVADOC_LINE_PATTERN.matcher(line);
            String body;
            if (matcher.matches()) {
                body = matcher.group(1);
            } else {
                body = line;
            }
            body = body.trim();
            if (buf.length() > 0) {
                buf.append('\n');
            }
            buf.append(body);
            if (body.length() > 0) {
                appended = true;
            }
        }
        if (!appended) {
            return null;
        }
        return buf.toString();
    }

    /**
     * 型宣言に{@link MappedSuperclass}注釈が付けられている場合{@code true}を返します。
     * 
     * @param node
     *            型宣言
     * @return {@link MappedSuperclass}注釈が付けられている場合{@code true}
     */
    protected boolean hasMappedSuperclassAnnotation(
            ClassOrInterfaceDeclaration node) {
        List<AnnotationExpr> annotations = node.getAnnotations();
        for (AnnotationExpr annotation : annotations) {
            String name = annotation.getName().asString();
            if (MappedSuperclass.class.getSimpleName().equals(name)
                    || MappedSuperclass.class.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Javadocコメントの読み取り結果を保持するクラスです。
     * 
     * @author taedium
     */
    public static class JavadocResult {

        /** クラスのコメント */
        public String classComment;

        /** フィールド名をキー、コメントを値とするマップ */
        public Map<String, String> fieldCommentMap;

        /** {@link MappedSuperclass}が付けられている場合{@code true} */
        public boolean mappedSuperclass;

        /**
         * インスタンスを構築します。
         * 
         * @param classComment
         *            クラスのコメント
         * @param fieldCommentMap
         *            フィールド名をキー、コメントを値とするマップ
         * @param mappedSuperclass
         *            {@link MappedSuperclass}が付けられている場合{@code true}
         */
        public JavadocResult(String classComment,
                Map<String, String> fieldCommentMap, boolean mappedSuperclass) {
            this.classComment = classComment;
            this.fieldCommentMap = fieldCommentMap;
            this.mappedSuperclass = mappedSuperclass;
        }
    }

}
