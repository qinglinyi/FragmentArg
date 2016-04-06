package com.qinglinyi.arg.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(value = RetentionPolicy.CLASS)
@Documented
public @interface Arg {

    String key() default "";
}
