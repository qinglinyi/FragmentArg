package com.qinglinyi.arg.compiler;

import com.qinglinyi.arg.api.Injector;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Generate ArgInjectorImpl
 *
 * @author qinglinyi
 * @since 1.0.0
 */
final class InjectorGenerator {

    private static final String CLASS_NAME = "ArgInjectorImpl";

    static TypeSpec generateClass(List<AnnotatedClass> classes) {
        TypeSpec.Builder builder = classBuilder(CLASS_NAME).addModifiers(PUBLIC, FINAL);
        builder.addSuperinterface(ClassName.get(Injector.class));
        builder.addMethod(maskInjectMethod(classes));
        return builder.build();
    }

    static MethodSpec maskInjectMethod(List<AnnotatedClass> classes) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("inject")
                .addModifiers(PUBLIC)
                .addParameter(ClassName.get(Object.class), "fragment");
        for (AnnotatedClass annotatedClass : classes) {
            TypeName fragment = ClassName.get(annotatedClass.getType());
            ClassName fragmentBuilder = ClassName.get(annotatedClass.packageName,
                    Util.builderName(annotatedClass));
            builder.addStatement("if(fragment.getClass() == $T.class) $T.inject(($T)$N)",
                    fragment,
                    fragmentBuilder,
                    fragment,
                    "fragment");
        }
        return builder.build();
    }
}
