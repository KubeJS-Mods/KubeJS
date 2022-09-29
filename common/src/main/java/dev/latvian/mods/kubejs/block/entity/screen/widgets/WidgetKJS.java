package dev.latvian.mods.kubejs.block.entity.screen.widgets;

import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.rhino.NativeArray;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class WidgetKJS {
	private static final Map<String, Function<CompoundTag, WidgetKJS>> registry = new HashMap<>();

	public static void register(String key, Function<CompoundTag, WidgetKJS> factory) {
		registry.put(key, factory);
	}

	public static Function<CompoundTag, WidgetKJS> getFactory(String key) {
		return registry.get(key);
	}

	public static WidgetKJS of(Object obj) {
		if (obj instanceof NativeArray) {
			List<WidgetKJS> eles = ListJS.orSelf(obj).stream().map(WidgetKJS::of).toList();
			WidgetKJS widget = new ContainerWidgetKJS(new CompoundTag());
			widget.children = eles;
			return widget;
		}
		Map<?, ?> map = MapJS.of(obj);
		if (map == null) return null;
		var type = map.get("type") instanceof String s ? s : null;
		if (type == null) return null;
		return getFactory(type).apply(NBTUtils.toTagCompound(map));
	}

	public WidgetKJS(CompoundTag opts) {
		// apply base styles
		// handle children

	}

	private List<WidgetKJS> children = new ArrayList<>();

	// render the element

}
