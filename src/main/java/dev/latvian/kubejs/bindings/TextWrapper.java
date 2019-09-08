package dev.latvian.kubejs.bindings;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.documentation.Param;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextString;
import dev.latvian.kubejs.text.TextTranslate;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@DocClass("Text Utilities")
public class TextWrapper
{
	@DocMethod("Creates text component from any object")
	public Text of(@Nullable Object object)
	{
		return Text.of(object);
	}

	@DocMethod("Joins text components together")
	public Text join(Text separator, Iterable<Text> texts)
	{
		return Text.join(separator, texts);
	}

	@DocMethod("Creates text component from JSON")
	public Text fromJson(JsonElement e)
	{
		return Text.fromJson(e);
	}

	@DocMethod(value = "Creates text component from string", params = @Param(type = String.class))
	public Text string(@Nullable Object text)
	{
		return new TextString(text);
	}

	@DocMethod("Creates text component from language key")
	public Text translate(String key)
	{
		return new TextTranslate(key, new Object[0]);
	}

	@DocMethod("Creates text component from language key and extra objects")
	public Text translate(String key, Object... objects)
	{
		return new TextTranslate(key, objects);
	}

	@DocMethod(value = "Black text", params = @Param(type = Text.class))
	public Text black(@Nullable Object text)
	{
		return of(text).black();
	}

	@DocMethod(value = "Dark blue text", params = @Param(type = Text.class))
	public Text darkBlue(@Nullable Object text)
	{
		return of(text).darkBlue();
	}

	@DocMethod(value = "Dark green text", params = @Param(type = Text.class))
	public Text darkGreen(@Nullable Object text)
	{
		return of(text).darkGreen();
	}

	@DocMethod(value = "Dark aqua text", params = @Param(type = Text.class))
	public Text darkAqua(@Nullable Object text)
	{
		return of(text).darkAqua();
	}

	@DocMethod(value = "Dark red text", params = @Param(type = Text.class))
	public Text darkRed(@Nullable Object text)
	{
		return of(text).darkRed();
	}

	@DocMethod(value = "Dark purple text", params = @Param(type = Text.class))
	public Text darkPurple(@Nullable Object text)
	{
		return of(text).darkPurple();
	}

	@DocMethod(value = "Gold text", params = @Param(type = Text.class))
	public Text gold(@Nullable Object text)
	{
		return of(text).gold();
	}

	@DocMethod(value = "Gray text", params = @Param(type = Text.class))
	public Text gray(@Nullable Object text)
	{
		return of(text).gray();
	}

	@DocMethod(value = "Dark gray text", params = @Param(type = Text.class))
	public Text darkGray(@Nullable Object text)
	{
		return of(text).darkGray();
	}

	@DocMethod(value = "Blue text", params = @Param(type = Text.class))
	public Text blue(@Nullable Object text)
	{
		return of(text).blue();
	}

	@DocMethod(value = "Green text", params = @Param(type = Text.class))
	public Text green(@Nullable Object text)
	{
		return of(text).green();
	}

	@DocMethod(value = "Aqua text", params = @Param(type = Text.class))
	public Text aqua(@Nullable Object text)
	{
		return of(text).aqua();
	}

	@DocMethod(value = "Red text", params = @Param(type = Text.class))
	public Text red(@Nullable Object text)
	{
		return of(text).red();
	}

	@DocMethod(value = "Light purple text", params = @Param(type = Text.class))
	public Text lightPurple(@Nullable Object text)
	{
		return of(text).lightPurple();
	}

	@DocMethod(value = "Yellow text", params = @Param(type = Text.class))
	public Text yellow(@Nullable Object text)
	{
		return of(text).yellow();
	}

	@DocMethod(value = "White text", params = @Param(type = Text.class))
	public Text white(@Nullable Object text)
	{
		return of(text).white();
	}
}