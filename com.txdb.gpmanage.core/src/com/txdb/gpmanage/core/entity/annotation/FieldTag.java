package com.txdb.gpmanage.core.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(value = { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldTag {

	String constraint();
	String constraintTable() default "";

	String description() default "";
}
