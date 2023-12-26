package dev.latvian.mods.kubejs.util;

import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassFilter {
	private static final byte V_DEF = -1;
	private static final byte V_DENY = 0;
	private static final byte V_ALLOW = 1;

	private final Set<String> denyStrong;
	private final List<String> denyWeak;
	private final Set<String> allowStrong;
	private final List<String> allowWeak;
	private final Object2ByteOpenHashMap<String> cache;

	public ClassFilter() {
		denyStrong = new HashSet<>();
		denyWeak = new ArrayList<>();
		allowStrong = new HashSet<>();
		allowWeak = new ArrayList<>();
		cache = new Object2ByteOpenHashMap<>();
		cache.defaultReturnValue(V_DEF);
	}

	public void deny(String s) {
		if ((s = s.trim()).isEmpty()) {
			return;
		}

		denyStrong.add(s);

		if (!denyWeak.contains(s)) {
			denyWeak.add(s);
		}
	}

	public void deny(Class<?> c) {
		deny(c.getName());
	}

	public void allow(String s) {
		if ((s = s.trim()).isEmpty()) {
			return;
		}

		allowStrong.add(s);

		if (!allowWeak.contains(s)) {
			allowWeak.add(s);
		}
	}

	public void allow(Class<?> c) {
		allow(c.getName());
	}

	private byte isAllowed0(String s) {
		if (denyStrong.contains(s)) {
			return V_DENY;
		}

		if (allowStrong.contains(s)) {
			return V_ALLOW;
		}

		for (var s1 : denyWeak) {
			if (s.startsWith(s1)) {
				return V_DENY;
			}
		}

		/*
		for (var s1 : allowWeak) {
			if (s.startsWith(s1)) {
				return V_ALLOW;
			}
		}
		 */

		return V_ALLOW;
	}

	public boolean isAllowed(String s) {
		var b = cache.getByte(s);

		if (b == V_DEF) {
			b = isAllowed0(s);
			cache.put(s, b);
		}

		return b == V_ALLOW;
	}
}
