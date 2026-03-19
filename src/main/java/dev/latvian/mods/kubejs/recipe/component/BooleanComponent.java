package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;

public class BooleanComponent implements RecipeComponent<Boolean> {
	private static final ResourceKey<RecipeComponentType<?>> TYPE = RecipeComponentType.builtin("boolean");

	public static final BooleanComponent BOOLEAN = new BooleanComponent();

	@Override
	public ResourceKey<RecipeComponentType<?>> type() {
		return TYPE;
	}

	@Override
	public Codec<Boolean> codec() {
		return Codec.BOOL;
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.BOOLEAN;
	}

	@Override
	public Boolean wrap(RecipeScriptContext cx, @Nullable Object from) {
		if (from instanceof Boolean n) {
			return n;
		} else if (from instanceof JsonPrimitive json) {
			return json.getAsBoolean();
		} else if (from instanceof CharSequence) {
			return Boolean.parseBoolean(from.toString());
		}

		throw new IllegalStateException("Expected a boolean!");
	}

	@Override
	public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
		return from instanceof Boolean || from instanceof JsonPrimitive json && json.isBoolean();
	}

	@Override
	public String toString() {
		return "boolean";
	}
}
