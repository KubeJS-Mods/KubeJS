package dev.latvian.kubejs.docs;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author LatvianModder
 * This annotation indicates that field type or method return type is a minecraft class and shouldn't be relied on
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface MinecraftClass {
}