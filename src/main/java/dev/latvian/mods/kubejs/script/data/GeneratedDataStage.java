package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.event.EventTargetType;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.util.StringRepresentable;

import java.util.Map;
import java.util.function.Function;

public enum GeneratedDataStage implements StringRepresentable {
	INTERNAL("internal", "Internal"),
	REGISTRIES("registries", "Registries"),
	BEFORE_MODS("before_mods", "Before Mods"),
	AFTER_MODS("after_mods", "After Mods"),
	LAST("last", "Last"),

	;

	public static final GeneratedDataStage[] FOR_SCRIPTS = {AFTER_MODS, BEFORE_MODS, LAST};
	public static final EventTargetType<GeneratedDataStage> TARGET = EventTargetType.fromEnum(GeneratedDataStage.class);

	public static <T> Map<GeneratedDataStage, T> forScripts(Function<GeneratedDataStage, T> factory) {
		var map = new Object2ObjectArrayMap<GeneratedDataStage, T>(FOR_SCRIPTS.length);

		for (var stage : FOR_SCRIPTS) {
			map.put(stage, factory.apply(stage));
		}

		return map;
	}

	public final String name;
	public final String displayName;

	GeneratedDataStage(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
