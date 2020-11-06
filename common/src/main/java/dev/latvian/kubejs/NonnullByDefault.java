package dev.latvian.kubejs;

import org.jetbrains.annotations.NotNull;

import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author LatvianModder
 * This annotation combines method and parameter nonnullability
 */
@Documented
@NotNull
@TypeQualifierDefault({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NonnullByDefault
{
}