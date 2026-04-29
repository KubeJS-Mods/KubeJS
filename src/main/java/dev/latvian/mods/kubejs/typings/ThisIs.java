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
	Class<?> value();
}
