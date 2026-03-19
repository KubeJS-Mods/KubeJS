package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import org.jspecify.annotations.Nullable;

public enum IgnoreComponent implements RecipeComponent<Object> {
	INSTANCE;

	private static final ResourceKey<RecipeComponentType<?>> TYPE = RecipeComponentType.builtin("ignore");

	@Override
	public ResourceKey<RecipeComponentType<?>> type() {
		return TYPE;
	}

	@Override
	public Codec<Object> codec() {
		return ExtraCodecs.JAVA;
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.NONE;
	}

	@Override
	@Nullable
	public Object wrap(RecipeScriptContext cx, @Nullable Object from) {
		return from;
	}

	@Override
	public void validate(RecipeValidationContext ctx, Object value) {
	}

	@Override
	public boolean allowEmpty() {
		return true;
	}

	@Override
	public boolean isEmpty(@Nullable Object value) {
		return value == null;
	}

	@Override
	public String toString() {
		return "ignored";
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, Object value) {
	}

	@Override
	public boolean isIgnored() {
		return true;
	}
}
