package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

public record TimeComponent(String name, long scale, Codec<TickDuration> codec) implements RecipeComponent<TickDuration> {
	public static final TimeComponent TICKS = new TimeComponent("ticks", 1L, TickDuration.CODEC);
	public static final TimeComponent SECONDS = new TimeComponent("seconds", 20L, TickDuration.SECONDS_CODEC);
	public static final TimeComponent MINUTES = new TimeComponent("minutes", 1200L, TickDuration.MINUTES_CODEC);
	public static final TimeComponent HOURS = new TimeComponent("hours", 1200L, TickDuration.HOURS_CODEC);

	@Override
	public Codec<TickDuration> codec() {
		return TickDuration.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return TickDuration.TYPE_INFO;
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof Number || from instanceof JsonPrimitive json && json.isNumber();
	}

	@Override
	public String toString() {
		return name;
	}
}
