package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.type.TypeInfo;

public record TimeComponent(RecipeComponentType<?> type, long scale, Codec<TickDuration> codec) implements RecipeComponent<TickDuration> {
	public static final RecipeComponentType<TickDuration> TICKS = RecipeComponentType.unit(KubeJS.id("ticks"), type -> new TimeComponent(type, 1L, TickDuration.CODEC));
	public static final RecipeComponentType<TickDuration> SECONDS = RecipeComponentType.unit(KubeJS.id("seconds"), type -> new TimeComponent(type, 20L, TickDuration.SECONDS_CODEC));
	public static final RecipeComponentType<TickDuration> MINUTES = RecipeComponentType.unit(KubeJS.id("minutes"), type -> new TimeComponent(type, 1200L, TickDuration.MINUTES_CODEC));
	public static final RecipeComponentType<TickDuration> HOURS = RecipeComponentType.unit(KubeJS.id("hours"), type -> new TimeComponent(type, 1200L, TickDuration.HOURS_CODEC));

	@Override
	public Codec<TickDuration> codec() {
		return TickDuration.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return TickDuration.TYPE_INFO;
	}

	@Override
	public boolean hasPriority(RecipeMatchContext cx, Object from) {
		return from instanceof Number || from instanceof JsonPrimitive json && json.isNumber();
	}

	@Override
	public TickDuration wrap(RecipeScriptContext cx, Object from) {
		if (from instanceof Number n) {
			return TickDuration.of((long) (n.doubleValue() * scale));
		} else {
			return TickDuration.wrap(from);
		}
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, TickDuration value) {
		builder.append(value.ticks() + "t");
	}

	@Override
	public boolean isEmpty(TickDuration value) {
		return value.ticks() <= 0L;
	}

	@Override
	public String toString() {
		return type.toString();
	}
}
