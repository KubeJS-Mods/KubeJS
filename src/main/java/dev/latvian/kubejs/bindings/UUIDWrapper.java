package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.util.UUIDUtilsJS;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
@DocClass(displayName = "UUID Utilities")
public class UUIDWrapper
{
	@DocMethod
	public String toString(UUID id)
	{
		return UUIDUtilsJS.toString(id);
	}

	@Nullable
	@DocMethod
	public UUID fromString(String string)
	{
		return UUIDUtilsJS.fromString(string);
	}
}