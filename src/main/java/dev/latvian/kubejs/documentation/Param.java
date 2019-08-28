package dev.latvian.kubejs.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LatvianModder
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ })
public @interface Param
{
	String value() default "";

	String info() default "";

	Class type() default Object.class;
}