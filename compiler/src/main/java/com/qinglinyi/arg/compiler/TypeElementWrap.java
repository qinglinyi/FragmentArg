package com.qinglinyi.arg.compiler;

import javax.lang.model.element.TypeElement;

public class TypeElementWrap {

    final TypeElement element;
    final boolean isSupper;

    public TypeElementWrap(TypeElement element, boolean isSupper) {
        this.element = element;
        this.isSupper = isSupper;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypeElementWrap) {
            return element.equals(((TypeElementWrap) obj).element);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return element.hashCode();
    }
}
