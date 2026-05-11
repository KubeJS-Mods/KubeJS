package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaMap;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.Map;

/**
 * A JS object exposing a Java readonly map. Attempt to write to the map will result in an exception that contains the script line.
 */
public class NativeJavaReadonlyMap extends NativeJavaMap {
	private final String errorMessage;

	/**
	 *
	 * @param errorMessage The error message to show when an attempt to put or delete an entry from this JS object is made.
	 */
	public NativeJavaReadonlyMap(Context cx, Scriptable scope, Object jo,
								 Map map, TypeInfo type, String errorMessage) {
		super(cx, scope, jo, map, type);
		this.errorMessage = errorMessage;
	}

	@Override
	public String getClassName() {
		return "JavaReadonlyMap";
	}

	@Override
	public void put(Context cx, String name, Scriptable start, Object value) {
		throw new KubeRuntimeException(errorMessage).source(SourceLine.of(cx));
	}

	@Override
	public void put(Context cx, int index, Scriptable start, Object value) {
		throw new KubeRuntimeException(errorMessage).source(SourceLine.of(cx));
	}

	@Override
	public void delete(Context cx, String name) {
		throw new KubeRuntimeException(errorMessage).source(SourceLine.of(cx));
	}

	@Override
	public void delete(Context cx, int index) {
		throw new KubeRuntimeException(errorMessage).source(SourceLine.of(cx));
	}


}
