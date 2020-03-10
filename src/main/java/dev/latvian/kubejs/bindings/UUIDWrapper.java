package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.util.UUIDUtilsJS;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class UUIDWrapper
{
	public String toString(UUID id)
	{
		return UUIDUtilsJS.toString(id);
	}

	@Nullable
	public UUID fromString(Object string)
	{
		return UUIDUtilsJS.fromString(String.valueOf(string));
	}
}