package com.qinglinyi.arg.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.type.TypeMirror;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Fragment Builder Generator
 * <p>
 * Generate corresponding Builder for Fragment
 *
 * @author qinglinyi
 * @since 1.0.0
 */
final class DetailBuilderGenerator {


    private static final String BUNDLE_NAME = "bundle";
    private static final ClassName BUNDLE_TYPE_NAME = ClassName.get("android.os", "Bundle");

    private DetailBuilderGenerator() {}

    public static TypeSpec generateClass(AnnotatedClass annotatedClass) {
        String className = Util.builderName(annotatedClass);
        TypeSpec.Builder classBuilder = classBuilder(className).addModifiers(PUBLIC, FINAL);
        ClassName fragmentType = ClassName.get(annotatedClass.packageName, annotatedClass.name);

        // Add Argument Bundle
        classBuilder.addField(FieldSpec
                .builder(BUNDLE_TYPE_NAME, BUNDLE_NAME, PRIVATE)
                .initializer(" new $T()", BUNDLE_TYPE_NAME)
                .build());

        // Add fragment
        classBuilder.addField(FieldSpec
                .builder(fragmentType, "fragment", PRIVATE)
                .build());

        // constructor
        classBuilder.addMethod(MethodSpec
                .constructorBuilder()
                .addModifiers(PUBLIC)
                .build());

        // constructor
        classBuilder.addMethod(MethodSpec
                .constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameter(fragmentType, "fragment")
                .addStatement("this.$N = fragment", "fragment")
                .build());

        // builder type
        ClassName builderClassType = ClassName.get(annotatedClass.packageName, className);

        for (AnnotatedFeild annotatedFeild : annotatedClass.argFields) {
            classBuilder.addMethod(makeAddMethod(builderClassType, annotatedFeild));
        }

        classBuilder.addMethod(makeBuildMethod(annotatedClass, false));
        classBuilder.addMethod(makeBuildMethod(annotatedClass, true));
        classBuilder.addMethod(makeInjectMethod(annotatedClass));

        return classBuilder.build();
    }

    private static MethodSpec makeAddMethod(TypeName returnTypeName, AnnotatedFeild
            annotatedFeild) {

        String operation = annotatedFeild.operation;
        String key = annotatedFeild.key;
        TypeMirror paramType = annotatedFeild.getElementType();

        return methodBuilder(key)
                .addModifiers(PUBLIC)
                .addParameter(TypeName.get(paramType), key)
                .addStatement("$N.put$N($S,$N)", BUNDLE_NAME, operation, key, key)
                .addStatement("return this")
                .returns(returnTypeName)
                .build();
    }


    private static MethodSpec makeBuildMethod(AnnotatedClass annotatedClass, boolean withFragmentParam) {
        TypeName fragmentTypeName = ClassName.get(annotatedClass.getType());
        MethodSpec.Builder builder = methodBuilder("build")
                .addModifiers(PUBLIC)
                .returns(fragmentTypeName);
        if (withFragmentParam) {
            builder.addParameter(ClassName.get(annotatedClass.getType()), "fragment");
        } else {
            builder.addStatement("if (this.fragment == null ) this.fragment = new $T()", fragmentTypeName);
        }
        builder.addStatement("fragment.setArguments(this.$N)", BUNDLE_NAME)
                .addStatement("return fragment");
        return builder.build();
    }

    /**
     * 创建静态的Inject，用来获取Fragment的Argument
     *
     * @param annotatedClass fragment
     * @return 方法
     */
    private static MethodSpec makeInjectMethod(AnnotatedClass annotatedClass) {

        MethodSpec.Builder builder = methodBuilder("inject")
                .addModifiers(PUBLIC, STATIC, FINAL)
                .addParameter(ClassName.get(annotatedClass.getType()), "fragment");

        if (annotatedClass.argFields == null || annotatedClass.argFields.size() == 0) {
            return builder.build();
        }

        builder.addStatement("if (fragment == null ) return")
                .addStatement("$T bundle = fragment.getArguments()", BUNDLE_TYPE_NAME)
                .addStatement("if (bundle == null) return");

        for (AnnotatedFeild argField : annotatedClass.argFields) {

            if (!"Serializable".equals(argField.operation)) {
                builder.addStatement("if (bundle.containsKey($S)) fragment.$N = bundle.get$N($S)",
                        argField.key,
                        argField.name,
                        argField.operation,
                        argField.key);
            } else {
                builder.addStatement("if (bundle.containsKey($S)) fragment.$N = ($T)bundle.get$N($S)",
                        argField.key,
                        argField.name,
                        ClassName.get(argField.getElementType()),
                        argField.operation,
                        argField.key);
            }
        }

        return builder.build();
    }
}
