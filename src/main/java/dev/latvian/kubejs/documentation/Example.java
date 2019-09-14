package dev.latvian.kubejs.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LatvianModder
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Example
{
	@Info("URL to example script. For gists use gist:<user id>:<gist id>:<filename.js>, e.g. gist:LatvianModder:9f5f8cb4121fda4141dbd2cfd5b3d5c9:clearlag.js")
	String value();
}