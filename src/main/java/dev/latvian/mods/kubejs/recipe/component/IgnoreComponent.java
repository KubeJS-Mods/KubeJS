package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;

public enum IgnoreComponent implements RecipeComponent<Object> {
	INSTANCE;

	public static final Object OBJECT = new Object();
	public static final Codec<Object> CODEC = Codec.unit(OBJECT);
	public static final RecipeComponentType<Object> TYPE = RecipeComponentType.unit(KubeJS.id("ignore"), IgnoreComponent.INSTANCE);

	@Override
	public RecipeComponentType<?> type() {
		return TYPE;
	}

	@Override
	public Codec<Object> codec() {
		return CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.NONE;
	}

	@Override
	public Object wrap(RecipeScriptContext cx, Object from) {
		return OBJECT;
	}

	@Override
	public void writeToJson(KubeRecipe recipe, RecipeComponentValue<Object> cv, JsonObject json) {
	}

	@Override
	public void readFromJson(KubeRecipe recipe, RecipeComponentValue<Object> cv, JsonObject json) {
	}

	@Override
	public void validate(RecipeValidationContext ctx, Object value) {
	}

	@Override
	public boolean allowEmpty() {
		return true;
	}

	@Override
	public boolean isEmpty(Object value) {
		return true;
	}

	@Override
	public String toString() {
		return "ignored";
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, Object value) {
	}

	@Override
	public String toString(OpsContainer ops, Object value) {
		return "ignore";
	}

	@Override
	public boolean isIgnored() {
		return true;
	}
}
