package dev.latvian.mods.kubejs.typings;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Annotation that typing generation mods can use to declare type guards.
///
/// Use this on `boolean`-returning methods that can guarantee that if the method returns `true`,
/// the current instance may be treated as the provided class type.
///
/// Type guards can help narrow down types in conditional blocks.
///
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface ThisIs {
	/**
	 * Alias for {@link #classes()}
	 *
	 * @see #classes()
	 * @see #classNames()
	 */
	Class<?>[] value() default {};

	Class<?>[] classes() default {};

	/**
	 * (Fully qualified) class names of types this object may be assigned to.
	 * <p>
	 * This **needs** to be used with client-only classes to avoid loading them,
	 * otherwise the annotation scanner will crash the game!
	 */
	String[] classNames() default {};
}
