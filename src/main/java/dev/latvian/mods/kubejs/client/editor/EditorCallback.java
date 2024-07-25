package dev.latvian.mods.kubejs.client.editor;

public interface EditorCallback<T> {
	void callback(T value, boolean success);
}
