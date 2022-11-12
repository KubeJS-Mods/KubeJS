package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Symbol;

public class LegacyCodeHandler extends BaseFunction {
	private static class LegacyError extends RuntimeException {
		public LegacyError(String message) {
			super(message);
		}

		@Override
		public String toString() {
			return getLocalizedMessage();
		}
	}

	public final String code;

	public LegacyCodeHandler(String code) {
		this.code = code;
	}

	public LegacyError makeError(Context cx) {
		int[] linep = {0};
		Context.getSourcePositionFromStack(cx, linep);
		return new LegacyError("Line " + linep[0] + ": '" + code + "' is no longer supported! Read more on wiki: https://kubejs.com/kjs6");
	}

	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		throw makeError(cx);
	}

	@Override
	public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
		throw makeError(cx);
	}

	@Override
	public void put(Context cx, String name, Scriptable start, Object value) {
		throw makeError(cx);
	}

	@Override
	public void put(Context cx, int index, Scriptable start, Object value) {
		throw makeError(cx);
	}

	@Override
	public void put(Context cx, Symbol key, Scriptable start, Object value) {
		throw makeError(cx);
	}

	@Override
	public Object get(Context cx, String name, Scriptable start) {
		throw makeError(cx);
	}

	@Override
	public Object get(Context cx, int index, Scriptable start) {
		throw makeError(cx);
	}

	@Override
	public Object get(Context cx, Symbol key, Scriptable start) {
		throw makeError(cx);
	}
}
