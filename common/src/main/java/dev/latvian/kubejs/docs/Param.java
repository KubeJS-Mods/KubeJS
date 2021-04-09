package dev.latvian.kubejs.docs;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LatvianModder
 * Annotate classes, fields, methods and constructors with this to include them in auto-generated documentation
 */
@Documented
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
	String key() default "";

	String value();
}
