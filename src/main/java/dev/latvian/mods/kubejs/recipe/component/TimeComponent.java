package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.DynamicRecipeComponent;
import dev.latvian.mods.kubejs.util.TimeJS;
import dev.latvian.mods.rhino.ScriptRuntime;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.JSObjectTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;

public record TimeComponent(String name, long scale) implements RecipeComponent<Long> {
	public static final TimeComponent TICKS = new TimeComponent("ticks", 1L);
	public static final TimeComponent SECONDS = new TimeComponent("seconds", 20L);
	public static final TimeComponent MINUTES = new TimeComponent("minutes", 1200L);

	public static final DynamicRecipeComponent DYNAMIC = new DynamicRecipeComponent(JSObjectTypeInfo.of(
		new JSObjectTypeInfo.Field("name", TypeInfo.STRING, true),
		new JSObjectTypeInfo.Field("scale", TypeInfo.LONG)
	), (cx, scope, args) -> {
		var name = String.valueOf(Wrapper.unwrapped(args.getOrDefault("name", "unnamed")));
		var scale = (long) ScriptRuntime.toNumber(cx, Wrapper.unwrapped(args.getOrDefault("scale", 1L)));
		return new TimeComponent(name, scale);
	});

	@Override
	public String componentType() {
		return name;
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.NUMBER.or(TypeInfo.STRING);
	}

	@Override
	public JsonElement write(KubeRecipe recipe, Long value) {
		return new JsonPrimitive(value / scale);
	}

	@Override
	public boolean hasPriority(KubeRecipe recipe, Object from) {
		return from instanceof Number || from instanceof JsonPrimitive json && json.isNumber();
	}

	@Override
	public Long read(KubeRecipe recipe, Object from) {
		if (from instanceof Number) {
			return ((Number) from).longValue() * scale;
		}

		return TimeJS.tickDurationOf(from);
	}

	@Override
	public String toString() {
		return componentType();
	}
}
