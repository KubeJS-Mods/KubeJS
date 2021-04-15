package dev.latvian.kubejs.util;

import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

import java.util.ArrayList;
import java.util.List;

public class ClassList {
	private static final byte V_DEF = -1;
	private static final byte V_DENY = 0;
	private static final byte V_ALLOW = 1;

	private final List<String> denied;
	private final List<String> allowed;
	private final Object2ByteOpenHashMap<String> cache;

	public ClassList() {
		denied = new ArrayList<>();
		allowed = new ArrayList<>();
		cache = new Object2ByteOpenHashMap<>();
		cache.defaultReturnValue(V_DEF);
	}

	public void deny(String s) {
		if (!denied.contains(s)) {
			denied.add(s);
		}
	}

	public void allow(String s) {
		if (!allowed.contains(s)) {
			allowed.add(s);
		}
	}

	private boolean isAllowed0(String s) {
		for (String s1 : denied) {
			if (s.startsWith(s1)) {
				return false;
			}
		}

		for (String s1 : allowed) {
			if (s.startsWith(s1)) {
				return true;
			}
		}

		return false;
	}

	public boolean isAllowed(String s) {
		byte b = cache.getByte(s);

		if (b == V_DEF) {
			b = isAllowed0(s) ? V_ALLOW : V_DENY;
			cache.put(s, b);
		}

		return b == V_ALLOW;
	}
}
