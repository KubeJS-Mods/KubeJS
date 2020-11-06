package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextColor;
import dev.latvian.kubejs.text.TextString;
import dev.latvian.kubejs.text.TextTranslate;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class TextWrapper
{
	public Text of(Object object)
	{
		return Text.of(object);
	}

	public Text join(Text separator, Iterable<Text> texts)
	{
		return Text.join(separator, texts);
	}

	public Text string(Object text)
	{
		return new TextString(text);
	}

	public Text translate(String key)
	{
		return new TextTranslate(key, new Object[0]);
	}

	public Text translate(String key, Object... objects)
	{
		return new TextTranslate(key, objects);
	}

	public Text black(Object text)
	{
		return of(text).black();
	}

	public Text darkBlue(Object text)
	{
		return of(text).darkBlue();
	}

	public Text darkGreen(Object text)
	{
		return of(text).darkGreen();
	}

	public Text darkAqua(Object text)
	{
		return of(text).darkAqua();
	}

	public Text darkRed(Object text)
	{
		return of(text).darkRed();
	}

	public Text darkPurple(Object text)
	{
		return of(text).darkPurple();
	}

	public Text gold(Object text)
	{
		return of(text).gold();
	}

	public Text gray(Object text)
	{
		return of(text).gray();
	}

	public Text darkGray(Object text)
	{
		return of(text).darkGray();
	}

	public Text blue(Object text)
	{
		return of(text).blue();
	}

	public Text green(Object text)
	{
		return of(text).green();
	}

	public Text aqua(Object text)
	{
		return of(text).aqua();
	}

	public Text red(Object text)
	{
		return of(text).red();
	}

	public Text lightPurple(Object text)
	{
		return of(text).lightPurple();
	}

	public Text yellow(Object text)
	{
		return of(text).yellow();
	}

	public Text white(Object text)
	{
		return of(text).white();
	}

	public Map<String, TextColor> getColors()
	{
		return TextColor.MAP;
	}
}