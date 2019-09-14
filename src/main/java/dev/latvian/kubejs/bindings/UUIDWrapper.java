package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.util.UUIDUtilsJS;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
@DisplayName("UUID Utilities")
public class UUIDWrapper
{
	public String toString(@P("id") UUID id)
	{
		return UUIDUtilsJS.toString(id);
	}

	@Nullable
	public UUID fromString(@P("string") @T(String.class) Object string)
	{
		return UUIDUtilsJS.fromString(String.valueOf(string));
	}
}