package dev.latvian.kubejs.docs;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LatvianModder
 * Annotate classes, fields, methods and constructors with this to include them in auto-generated documentation
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Doc {
	String value() default "";

	String name() default "";

	String path() default "";

	Param[] params() default {};
}
