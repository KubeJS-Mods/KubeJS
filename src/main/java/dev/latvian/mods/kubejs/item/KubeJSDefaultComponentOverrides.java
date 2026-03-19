package dev.latvian.mods.kubejs.item;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

public final class KubeJSDefaultComponentOverrides {
	private static final Object REMOVE = new Object();
	private static final Map<Item, Map<DataComponentType<?>, Object>> OVERRIDES = new IdentityHashMap<>();

	private KubeJSDefaultComponentOverrides() {
	}

	public static <T> void override(Item item, DataComponentType<T> type, @Nullable T value) {
		OVERRIDES.computeIfAbsent(item, k -> new IdentityHashMap<>())
			.put(type, value == null ? REMOVE : value);
	}

	public static void applyTo(ModifyDefaultComponentsEvent event) {
		if (OVERRIDES.isEmpty()) {
			return;
		}

		for (var entry : OVERRIDES.entrySet()) {
			var item = entry.getKey();
			var changes = entry.getValue();

			event.modify(item, patch -> {
				for (var e : changes.entrySet()) {
					var type = e.getKey();
					var v = e.getValue();

					if (v == REMOVE) {
						patch.set((DataComponentType<Object>) type, null);
					} else {
						patch.set((DataComponentType<Object>) type, v);
					}

				}
			});
		}
	}
}
