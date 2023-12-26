package dev.latvian.mods.kubejs.typings;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that document generation mods can use to read comments at runtime.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface Info {
	/**
	 * Type, method, field or record component description
	 */
	String value() default "";

	/**
	 * Params (only checked by methods and types)
	 */
	Param[] params() default {};
}
