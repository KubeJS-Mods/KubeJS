package dev.latvian.kubejs.docs;

import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author LatvianModder
 * This annotation indicates that field type or method return type is a minecraft class and shouldn't be relied on
 */
@Documented
@TypeQualifierDefault({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinecraftClass {
}