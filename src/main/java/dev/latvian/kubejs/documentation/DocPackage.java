package dev.latvian.kubejs.documentation;

import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author LatvianModder
 */
@Documented
@TypeQualifierDefault(ElementType.PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DocPackage
{
	Class[] value();
}