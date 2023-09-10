package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.event.EventJS;

import java.util.Map;
import java.util.regex.Pattern;

public class LangEventJS extends EventJS {
	public static final Pattern PATTERN = Pattern.compile("[a-z_]+");

	public record Key(String namespace, String lang, String key) {
	}

	public final String lang;
	public final Map<Key, String> map;

	public LangEventJS(String lang, Map<Key, String> map) {
		this.lang = lang;
		this.map = map;
	}

	public void add(String namespace, String key, String value) {
		map.put(new Key(namespace, lang, key), value);
	}

	public void addAll(String namespace, Map<String, String> map) {
		for (var e : map.entrySet()) {
			add(namespace, e.getKey(), e.getValue());
		}
	}

	public void add(String key, String value) {
		add("minecraft", key, value);
	}

	public void addAll(Map<String, String> map) {
		addAll("minecraft", map);
	}
}
