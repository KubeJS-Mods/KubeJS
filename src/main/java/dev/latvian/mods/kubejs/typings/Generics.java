package dev.latvian.mods.kubejs.typings;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provide list of generic types when runtime erases them, usually for method params
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.RECORD_COMPONENT, ElementType.PARAMETER})
public @interface Generics {
	Class<?>[] value();

	Class<?> base() default Object.class;
}
