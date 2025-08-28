package dev.latvian.mods.kubejs.util;

import java.util.ArrayList;

public class ErrorStack {
	public static final ErrorStack NONE = new ErrorStack() {
		@Override
		public void push(Object parent) {
		}

		@Override
		public void setKey(Object key) {
		}

		@Override
		public void setKey(int index) {
		}

		@Override
		public void pop() {
		}
	};

	private final ArrayList<Object> parents;
	private final ArrayList<Object> keys;

	public ErrorStack() {
		this.parents = new ArrayList<>(2);
		this.keys = new ArrayList<>(2);
	}

	public void push(Object parent) {
		parents.add(parent);
		keys.add("?");
	}

	public void setKey(Object key) {
		keys.set(keys.size() - 1, key);
	}

	public void setKey(int index) {
		keys.set(keys.size() - 1, index);
	}

	public void pop() {
		parents.removeLast();
		keys.removeLast();
	}

	@Override
	public String toString() {
		if (keys.size() <= 1) {
			return "";
		}

		var sb = new StringBuilder();

		for (var key : keys) {
			sb.append('[');
			sb.append(key);
			sb.append(']');
		}

		return sb.toString();
	}

	public String atString() {
		var str = toString();
		return str.isEmpty() ? "" : (" @ " + str);
	}

	public String stringAt() {
		var str = toString();
		return str.isEmpty() ? "" : (str + " @ ");
	}
}
