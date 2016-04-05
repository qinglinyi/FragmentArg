package com.qinglinyi.arg.compiler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

final class OperationUtil {

    private OperationUtil() {
    }

    private static final Map<String, String> ARGUMENT_TYPES = new HashMap<>();

    static {
        ARGUMENT_TYPES.put("java.lang.String", "String");
        ARGUMENT_TYPES.put("int", "Int");
        ARGUMENT_TYPES.put("java.lang.Integer", "Int");
        ARGUMENT_TYPES.put("long", "Long");
        ARGUMENT_TYPES.put("java.lang.Long", "Long");
        ARGUMENT_TYPES.put("double", "Double");
        ARGUMENT_TYPES.put("java.lang.Double", "Double");
        ARGUMENT_TYPES.put("short", "Short");
        ARGUMENT_TYPES.put("java.lang.Short", "Short");
        ARGUMENT_TYPES.put("float", "Float");
        ARGUMENT_TYPES.put("java.lang.Float", "Float");
        ARGUMENT_TYPES.put("byte", "Byte");
        ARGUMENT_TYPES.put("java.lang.Byte", "Byte");
        ARGUMENT_TYPES.put("boolean", "Boolean");
        ARGUMENT_TYPES.put("java.lang.Boolean", "Boolean");
        ARGUMENT_TYPES.put("char", "Char");
        ARGUMENT_TYPES.put("java.lang.Character", "Char");
        ARGUMENT_TYPES.put("java.lang.CharSequence", "CharSequence");
        ARGUMENT_TYPES.put("android.os.Bundle", "Bundle");
        ARGUMENT_TYPES.put("android.os.Parcelable", "Parcelable");
    }

    static String getOperation(ProcessingEnvironment env, Element feildElement) {

        TypeMirror type = feildElement.asType();
        String typeStr = type.toString();

        String op = ARGUMENT_TYPES.get(Util.getRawType(typeStr));
        if (op != null) {
            if (Util.isArray(typeStr)) {
                return op + "Array";
            } else {
                return op;
            }
        }

        Types typeUtils = env.getTypeUtils();

        String[] arrayListTypes = new String[]{
                String.class.getName(),
                Integer.class.getName(),
                CharSequence.class.getName()
        };

        String[] arrayListOps =
                new String[]{"StringArrayList", "IntegerArrayList", "CharSequenceArrayList"};
        for (int i = 0; i < arrayListTypes.length; i++) {
            TypeMirror tm = getArrayListType(env, arrayListTypes[i]);
            if (typeUtils.isAssignable(type, tm)) {
                return arrayListOps[i];
            }
        }

        if (typeUtils.isAssignable(type,
                getWildcardType(env, "java.util.ArrayList", "android.os.Parcelable"))) {
            return "ParcelableArrayList";
        }

        TypeMirror sparseParcelableArray =
                getWildcardType(env, "android.util.SparseArray", "android.os.Parcelable");
        if (typeUtils.isAssignable(type, sparseParcelableArray)) {
            return "SparseParcelableArray";
        }

        Elements elements = env.getElementUtils();
        if (typeUtils.isAssignable(type, elements.getTypeElement(Serializable.class.getName()).asType())) {
            return "Serializable";
        }
        if (typeUtils.isAssignable(type, elements.getTypeElement("android.os.Parcelable").asType())) {
            return "Parcelable";
        }
        return null;
    }

    static TypeMirror getWildcardType(ProcessingEnvironment env, String type, String elementType) {
        TypeElement arrayList = env.getElementUtils().getTypeElement(type);
        TypeMirror elType = env.getElementUtils().getTypeElement(elementType).asType();
        final Types typeUtils = env.getTypeUtils();
        return typeUtils.getDeclaredType(arrayList, typeUtils.getWildcardType(elType, null));
    }

    static TypeMirror getArrayListType(ProcessingEnvironment env, String elementType) {
        TypeElement arrayList = env.getElementUtils().getTypeElement("java.util.ArrayList");
        TypeMirror elType = env.getElementUtils().getTypeElement(elementType).asType();
        return env.getTypeUtils().getDeclaredType(arrayList, elType);
    }
}
