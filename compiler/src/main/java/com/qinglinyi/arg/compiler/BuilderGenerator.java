package com.qinglinyi.arg.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * @author qinglinyi
 * @since 1.0.0
 */
final class BuilderGenerator {


    private static final String CLASS_NAME = "FragmentBuilder";


    public static TypeSpec generateClass(List<AnnotatedClass> classes) {
        TypeSpec.Builder builder = classBuilder(CLASS_NAME).addModifiers(PUBLIC, FINAL);
        builder.addMethod(MethodSpec.constructorBuilder().addModifiers(PRIVATE).build());
        for (AnnotatedClass annotate : classes) {
            if (!annotate.isSupperClass) {
                builder.addMethod(maskBuilderMethod(annotate));
            }
        }
        return builder.build();
    }

    public static MethodSpec maskBuilderMethod(AnnotatedClass annotatedClass) {
        ClassName fragmentBuilder = ClassName.get(annotatedClass.packageName,
                Util.builderName(annotatedClass));
        return MethodSpec.methodBuilder("builder")
                .addModifiers(PUBLIC, STATIC)
                .addParameter(ClassName.get(annotatedClass.getType()), "fragment")
                .addStatement("return  new $T (fragment)", fragmentBuilder)
                .returns(fragmentBuilder)
                .build();
    }


}
