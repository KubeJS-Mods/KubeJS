package dev.latvian.kubejs.util;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class FieldJS<T>
{
	private final Field field;

	public FieldJS(@Nullable Field f)
	{
		field = f;
	}

	public Optional<T> get(@Nullable Object object)
	{
		if (field == null)
		{
			return Optional.empty();
		}

		try
		{
			if (!field.isAccessible())
			{
				field.setAccessible(true);
			}

			return Optional.ofNullable(UtilsJS.cast(field.get(object)));
		}
		catch (Exception ex)
		{
			return Optional.empty();
		}
	}

	@Nullable
	public Optional<T> staticGet()
	{
		return get(null);
	}

	public boolean set(@Nullable Object object, @Nullable Object value)
	{
		if (field == null)
		{
			return false;
		}

		try
		{
			if (!field.isAccessible())
			{
				field.setAccessible(true);
			}

			field.set(object, value);
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
	}

	public boolean staticSet(@Nullable Object value)
	{
		return set(null, value);
	}
}