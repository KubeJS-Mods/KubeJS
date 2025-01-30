package dev.latvian.mods.kubejs.util;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation combines method and parameter nonnullability
 */
@Documented
@NotNull
@Retention(RetentionPolicy.CLASS)
public @interface NonnullByDefault {
}