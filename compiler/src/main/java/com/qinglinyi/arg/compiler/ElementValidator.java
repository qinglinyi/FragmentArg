package com.qinglinyi.arg.compiler;

import javax.lang.model.element.Element;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

final class ElementValidator {
    static boolean isPublic(Element annotatedClass) {
        return annotatedClass.getModifiers().contains(PUBLIC);
    }

    static boolean isAbstract(Element annotatedClass) {
        return annotatedClass.getModifiers().contains(ABSTRACT);
    }

    static boolean isPrivate(Element annotatedClass) {
        return annotatedClass.getModifiers().contains(PRIVATE);
    }

    static boolean isDefault(Element annotatedClass) {
        return annotatedClass.getModifiers().contains(DEFAULT);
    }

    static boolean isFinal(Element annotatedClass) {
        return annotatedClass.getModifiers().contains(FINAL);
    }
}
