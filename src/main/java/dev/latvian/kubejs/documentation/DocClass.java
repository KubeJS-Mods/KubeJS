package dev.latvian.kubejs.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LatvianModder
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@DocClass("Annotation you put on documented classes")
public @interface DocClass
{
	@DocMethod("Information")
	String value() default "";

	@DocMethod("Custom display name")
	String displayName() default "";
}