package com.qinglinyi.arg.compiler;

import javax.lang.model.element.Element;

/**
 * @author qinglinyi
 * @since 1.0.0
 */
public class ArgException extends Exception {

    private Element element;
    private String message;
    private Object[] messageArgs;

    public ArgException(Element element, String message, Object... messageArgs) {
        this.element = element;
        this.message = message;
        this.messageArgs = messageArgs;
    }

    public Element getElement() {
        return element;
    }

    @Override public String getMessage() {
        return message;
    }

    public Object[] getMessageArgs() {
        return messageArgs;
    }
}
