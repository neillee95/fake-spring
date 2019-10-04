package me.lee.fakespring.framework.web.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {

    String value();

    boolean required() default true;

    String defaultValue() default "";

}
