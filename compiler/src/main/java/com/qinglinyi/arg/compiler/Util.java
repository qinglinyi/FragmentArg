package com.qinglinyi.arg.compiler;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

final class Util {

    static final String BUILDER_PACKAGE_NAME = "com.qinglinyi.arg";
    static final String CLASS_NAME_SUFFIX = "Builder";

    private Util() {
    }

    static String getPackageName(Elements elementUtils, TypeElement type)
            throws ArgException {
        PackageElement pkg = elementUtils.getPackageOf(type);
        if (pkg.isUnnamed()) {
            throw new ArgException(type, "The package of %s has no name", type.getSimpleName());
        }
        return pkg.getQualifiedName().toString();
    }

    static String getRawType(String type) {
        if (isArray(type)) {
            return type.substring(0, type.length() - 2);
        }
        return type;
    }

    static boolean isArray(String type) {
        return type.endsWith("[]");
    }

    static String builderName(AnnotatedClass annotatedClass) {
        return annotatedClass.name + CLASS_NAME_SUFFIX;
    }




}
