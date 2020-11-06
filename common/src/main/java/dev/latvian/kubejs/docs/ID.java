package dev.latvian.kubejs.docs;

import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author LatvianModder
 * This annotation indicates that type is Identifier, a.k.a minecraft styled namespace:path string. Value is default namespace
 */
@Documented
@TypeQualifierDefault({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ID
{
	String value() default "minecraft";
}