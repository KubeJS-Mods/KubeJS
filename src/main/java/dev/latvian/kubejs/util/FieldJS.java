package dev.latvian.kubejs.util;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * @author LatvianModder
 */
public class FieldJS
{
	private final Field field;

	public FieldJS(@Nullable Field f)
	{
		field = f;
	}

	@Nullable
	public Object get(@Nullable Object object)
	{
		if (field == null)
		{
			return null;
		}

		try
		{
			if (!field.isAccessible())
			{
				field.setAccessible(true);
			}

			return field.get(object);
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	@Nullable
	public Object getStatic()
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

	public boolean setStatic(@Nullable Object value)
	{
		return set(null, value);
	}
}