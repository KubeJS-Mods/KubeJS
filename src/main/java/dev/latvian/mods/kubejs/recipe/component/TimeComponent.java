package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;

public record TimeComponent(ResourceKey<RecipeComponentType<?>> type, long scale, Codec<TickDuration> codec) implements RecipeComponent<TickDuration> {
	public static final TimeComponent TICKS = new TimeComponent(RecipeComponentType.builtin("ticks"), 1L, TickDuration.CODEC);
	public static final TimeComponent SECONDS = new TimeComponent(RecipeComponentType.builtin("seconds"), 20L, TickDuration.SECONDS_CODEC);
	public static final TimeComponent MINUTES = new TimeComponent(RecipeComponentType.builtin("minutes"), 1200L, TickDuration.MINUTES_CODEC);
	public static final TimeComponent HOURS = new TimeComponent(RecipeComponentType.builtin("hours"), 1200L, TickDuration.HOURS_CODEC);

	@Override
	public Codec<TickDuration> codec() {
		return TickDuration.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return TickDuration.TYPE_INFO;
	}

	@Override
	public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
		return from instanceof Number || from instanceof JsonPrimitive json && json.isNumber();
	}

	@Override
	public TickDuration wrap(RecipeScriptContext cx, @Nullable Object from) {
		if (from instanceof Number n) {
			return TickDuration.of((long) (n.doubleValue() * scale));
		} else {
			return TickDuration.wrap(cx.cx(), from);
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
