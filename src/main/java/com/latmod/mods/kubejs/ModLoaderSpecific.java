package com.latmod.mods.kubejs;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author LatvianModder
 * This annotation indicates that something is modloader specific, and should not be used in scripts, but more for internal use
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ModLoaderSpecific
{
}