package dev.latvian.mods.kubejs.typings;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>An annotation that is typically used in builder classes.</p>
 * <p>On methods: Mark it as one that always returns its owner class.</p>
 * <p>On classes: Mark all methods where their return type == {@link #value()} as ones that return the owner class.</p>
 * <p>If {@link #value()} == {@link Object}.class, then use the owner class.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Repeatable(ReturnsSelfContainer.class)
public @interface ReturnsSelf {
	Class<?> value() default Object.class;
}
