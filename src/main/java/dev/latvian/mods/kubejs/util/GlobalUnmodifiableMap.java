package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * An unmodifiable Map backing `global` in server and startup scripts.
 * It delegates its operations to the underlying unmodifiable map.
 * Its exceptions will contain a message explaining what went wrong.
 */
public class GlobalUnmodifiableMap<K, V> implements Map<K, V>, CustomJavaToJsWrapper {
	private static final String MESSAGE = "'global' cannot be assigned to in client or server scripts";
	private final Map<K, V> unmodifiableMap;

	public GlobalUnmodifiableMap(Map<K, V> map) {
		this.unmodifiableMap = Collections.unmodifiableMap(map);
	}

	@Override
	public int size() {
		return unmodifiableMap.size();
	}

	@Override
	public boolean isEmpty() {
		return unmodifiableMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return unmodifiableMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return unmodifiableMap.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return unmodifiableMap.get(key);
	}

	@Override
	public @Nullable V put(K key, V value) {
		throw new KubeRuntimeException(MESSAGE);
	}

	public @Nullable V put(Context cx, K key, V value) {
		throw new KubeRuntimeException(MESSAGE).source(SourceLine.of(cx));
	}

	@Override
	public V remove(Object key) {
		throw new KubeRuntimeException(MESSAGE);
	}

	public V remove(Context cx, Object key) {
		throw new KubeRuntimeException(MESSAGE).source(SourceLine.of(cx));
	}

	@Override
	public void putAll(@NotNull Map<? extends K, ? extends V> m) {
		throw new KubeRuntimeException(MESSAGE);
	}

	public void putAll(Context cx, @NotNull Map<? extends K, ? extends V> m) {
		throw new KubeRuntimeException(MESSAGE).source(SourceLine.of(cx));
	}

	@Override
	public void clear() {
		throw new KubeRuntimeException(MESSAGE);
	}

	public void clear(Context cx) {
		throw new KubeRuntimeException(MESSAGE).source(SourceLine.of(cx));
	}

	@Override
	public @NotNull Set<K> keySet() {
		return unmodifiableMap.keySet();
	}

	@Override
	public @NotNull Collection<V> values() {
		return unmodifiableMap.values();
	}

	@Override
	public @NotNull Set<Entry<K, V>> entrySet() {
		return unmodifiableMap.entrySet();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return unmodifiableMap.hashCode();
	}

	public String toString() {
		return unmodifiableMap.toString();
	}

	@Override
	public Scriptable convertJavaToJs(Context cx, Scriptable scope, TypeInfo staticType) {
		return new NativeJavaReadonlyMap(cx, scope, this, this, staticType, MESSAGE);
	}
}
