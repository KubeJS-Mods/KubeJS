package dev.latvian.mods.kubejs.typings;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used in {@link JsInfo} to document method params, dependent on order they're added in
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface JsParam {
	/**
	 * Override param name
	 */
	String name() default "";

	/**
	 * Parameter description
	 */
	String value() default "";
}
