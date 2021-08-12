package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextString;
import dev.latvian.kubejs.text.TextTranslate;

/**
 * @author LatvianModder
 */
public class TextWrapper {
	public static Text of(Object object) {
		return Text.of(object);
	}

	public static Text join(Text separator, Iterable<Text> texts) {
		return Text.join(separator, texts);
	}

	public static Text string(Object text) {
		return new TextString(text);
	}

	public static Text translate(String key) {
		return new TextTranslate(key, new Object[0]);
	}

	public static Text translate(String key, Object... objects) {
		return new TextTranslate(key, objects);
	}

	public static Text black(Object text) {
		return of(text).black();
	}

	public static Text darkBlue(Object text) {
		return of(text).darkBlue();
	}

	public static Text darkGreen(Object text) {
		return of(text).darkGreen();
	}

	public static Text darkAqua(Object text) {
		return of(text).darkAqua();
	}

	public static Text darkRed(Object text) {
		return of(text).darkRed();
	}

	public static Text darkPurple(Object text) {
		return of(text).darkPurple();
	}

	public static Text gold(Object text) {
		return of(text).gold();
	}

	public static Text gray(Object text) {
		return of(text).gray();
	}

	public static Text darkGray(Object text) {
		return of(text).darkGray();
	}

	public static Text blue(Object text) {
		return of(text).blue();
	}

	public static Text green(Object text) {
		return of(text).green();
	}

	public static Text aqua(Object text) {
		return of(text).aqua();
	}

	public static Text red(Object text) {
		return of(text).red();
	}

	public static Text lightPurple(Object text) {
		return of(text).lightPurple();
	}

	public static Text yellow(Object text) {
		return of(text).yellow();
	}

	public static Text white(Object text) {
		return of(text).white();
	}
}