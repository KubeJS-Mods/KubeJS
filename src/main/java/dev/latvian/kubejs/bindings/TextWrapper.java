package dev.latvian.kubejs.bindings;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.Info;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextColor;
import dev.latvian.kubejs.text.TextString;
import dev.latvian.kubejs.text.TextTranslate;

import java.util.Map;

/**
 * @author LatvianModder
 */
@DisplayName("TextUtilities")
public class TextWrapper
{
	@Info("Creates text component from any object")
	public Text of(Object object)
	{
		return Text.of(object);
	}

	@Info("Joins text components together")
	public Text join(Text separator, Iterable<Text> texts)
	{
		return Text.join(separator, texts);
	}

	@Info("Creates text component from JSON")
	public Text fromJson(JsonElement e)
	{
		return Text.fromJson(e);
	}

	@Info("Creates text component from string")
	public Text string(@P("text") @T(String.class) Object text)
	{
		return new TextString(text);
	}

	@Info("Creates text component from language key")
	public Text translate(@P("key") String key)
	{
		return new TextTranslate(key, new Object[0]);
	}

	@Info("Creates text component from language key and extra objects")
	public Text translate(@P("key") String key, @P("objects") Object... objects)
	{
		return new TextTranslate(key, objects);
	}

	@Info("Black text")
	public Text black(@P("text") @T(Text.class) Object text)
	{
		return of(text).black();
	}

	@Info("Dark blue text")
	public Text darkBlue(@P("text") @T(Text.class) Object text)
	{
		return of(text).darkBlue();
	}

	@Info("Dark green text")
	public Text darkGreen(@P("text") @T(Text.class) Object text)
	{
		return of(text).darkGreen();
	}

	@Info("Dark aqua text")
	public Text darkAqua(@P("text") @T(Text.class) Object text)
	{
		return of(text).darkAqua();
	}

	@Info("Dark red text")
	public Text darkRed(@P("text") @T(Text.class) Object text)
	{
		return of(text).darkRed();
	}

	@Info("Dark purple text")
	public Text darkPurple(@P("text") @T(Text.class) Object text)
	{
		return of(text).darkPurple();
	}

	@Info("Gold text")
	public Text gold(@P("text") @T(Text.class) Object text)
	{
		return of(text).gold();
	}

	@Info("Gray text")
	public Text gray(@P("text") @T(Text.class) Object text)
	{
		return of(text).gray();
	}

	@Info("Dark gray text")
	public Text darkGray(@P("text") @T(Text.class) Object text)
	{
		return of(text).darkGray();
	}

	@Info("Blue text")
	public Text blue(@P("text") @T(Text.class) Object text)
	{
		return of(text).blue();
	}

	@Info("Green text")
	public Text green(@P("text") @T(Text.class) Object text)
	{
		return of(text).green();
	}

	@Info("Aqua text")
	public Text aqua(@P("text") @T(Text.class) Object text)
	{
		return of(text).aqua();
	}

	@Info("Red text")
	public Text red(@P("text") @T(Text.class) Object text)
	{
		return of(text).red();
	}

	@Info("Light purple text")
	public Text lightPurple(@P("text") @T(Text.class) Object text)
	{
		return of(text).lightPurple();
	}

	@Info("Yellow text")
	public Text yellow(@P("text") @T(Text.class) Object text)
	{
		return of(text).yellow();
	}

	@Info("White text")
	public Text white(@P("text") @T(Text.class) Object text)
	{
		return of(text).white();
	}

	public Map<String, TextColor> getColors()
	{
		return TextColor.MAP;
	}
}