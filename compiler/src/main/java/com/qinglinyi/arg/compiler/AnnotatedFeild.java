package com.qinglinyi.arg.compiler;

import com.qinglinyi.arg.api.Arg;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * Arg 对应的注解字段
 *
 * @author qinglinyi
 * @since 1.0.0
 */
class AnnotatedFeild implements Comparable<AnnotatedFeild> {

    public final String name;// 该字段名字
    public final String key;// 字段对应Argument的Key
    public final Element element;// 对应Element
    public final String operation;// 操作方法


    public AnnotatedFeild(Element element, Arg annotation, String operation) {
        this.name = element.getSimpleName().toString();
        this.key = getKey(element, annotation);
        this.element = element;
        this.operation = operation;
    }

    /**
     * 获取Argument的Key
     *
     * @param element    Arg对应的Element
     * @param annotation Arg注解
     * @return Key
     */
    private static String getKey(Element element, Arg annotation) {
        String field = element.getSimpleName().toString();
        if (!"".equals(annotation.key())) {
            return annotation.key();
        }
        return field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnnotatedFeild)) return false;

        AnnotatedFeild that = (AnnotatedFeild) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public TypeMirror getElementType() {
        return element.asType();
    }

    @Override
    public int compareTo(AnnotatedFeild o) {
        return name.compareTo(o.name);
    }

}
