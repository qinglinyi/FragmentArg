package com.qinglinyi.arg.compiler;

import java.util.Set;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * UseArg 对应的注解类（Fragment）
 *
 * @author qinglinyi
 * @since 1.0.0
 */
class AnnotatedClass {

    public final String name;// 类名字
    public final Set<AnnotatedFeild> argFields;// 字段set
    public final TypeElement typeElement;// 类型
    public final String packageName;// fragment 包名
    public final boolean isSupperClass;

    public AnnotatedClass(TypeElement typeElement, String packageName, boolean isSupperClass,
                          Set<AnnotatedFeild> argFields) {
        this.name = typeElement.getSimpleName().toString();
        this.typeElement = typeElement;
        this.argFields = argFields;
        this.packageName = packageName;
        this.isSupperClass = isSupperClass;
    }

    public TypeMirror getType() {
        return typeElement.asType();
    }

}
