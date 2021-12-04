package dev.latvian.mods.kubejs.script;

import dev.architectury.annotations.ForgeEvent;
import dev.latvian.mods.kubejs.util.WithAttachedData;

/**
 * @author LatvianModder
 * @deprecated This class and others like it will be changed significantly in 4.1,
 * including the removal of {@code EVENT} and the {@code @ForgeEvent}
 * annotation, honestly, just use the KubeJS plugin system instead...
 */
@Deprecated
@ForgeEvent
public class AttachDataEvent<T extends WithAttachedData> {
	private final DataType<T> type;
	private final T parent;

	public AttachDataEvent(DataType<T> t, T p) {
		type = t;
		parent = p;
	}

	public DataType<T> getType() {
		return type;
	}

	public T getParent() {
		return parent;
	}

	public void add(String id, Object object) {
		parent.getData().put(id, object);
	}
}