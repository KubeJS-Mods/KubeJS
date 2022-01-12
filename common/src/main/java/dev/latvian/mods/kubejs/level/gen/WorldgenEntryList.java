package dev.latvian.mods.kubejs.level.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class WorldgenEntryList {
	public List<String> values = new ArrayList<>();
	public boolean blacklist = false;

	public boolean verify(Predicate<String> filter) {
		if (values.isEmpty()) {
			return true;
		}

		for (var v : values) {
			if (filter.test(v)) {
				return !blacklist;
			}
		}

		return blacklist;
	}

	public boolean verify(String contains) {
		return verify(s -> s.equals(contains));
	}

	public boolean verifyIgnoreCase(String contains) {
		return verify(s -> s.equalsIgnoreCase(contains));
	}
}
