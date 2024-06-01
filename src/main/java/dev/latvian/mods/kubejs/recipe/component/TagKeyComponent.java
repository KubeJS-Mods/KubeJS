package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.DynamicRecipeComponent;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.JSObjectTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public record TagKeyComponent<T>(ResourceKey<? extends Registry<T>> registry, TypeInfo registryType) implements RecipeComponent<TagKey<T>> {
	private static final TypeInfo TAG_KEY_TYPE = TypeInfo.of(TagKey.class);

	public static final RecipeComponent<TagKey<Block>> BLOCK = new TagKeyComponent<>(Registries.BLOCK, TypeInfo.of(Block.class));
	public static final RecipeComponent<TagKey<Item>> ITEM = new TagKeyComponent<>(Registries.ITEM, TypeInfo.of(Item.class));
	public static final RecipeComponent<TagKey<EntityType<?>>> ENTITY_TYPE = new TagKeyComponent<>(Registries.ENTITY_TYPE, TypeInfo.of(EntityType.class));
	public static final RecipeComponent<TagKey<Biome>> BIOME = new TagKeyComponent<>(Registries.BIOME, TypeInfo.of(Biome.class));
	public static final RecipeComponent<TagKey<Fluid>> FLUID = new TagKeyComponent<>(Registries.FLUID, TypeInfo.of(Fluid.class));

	public static final DynamicRecipeComponent DYNAMIC = new DynamicRecipeComponent(JSObjectTypeInfo.of(
		new JSObjectTypeInfo.Field("registry", TypeInfo.STRING)
	), (cx, scope, args) -> {
		var registry = ResourceKey.createRegistryKey(ID.mc(Wrapper.unwrapped(args.get("registry"))));
		var r = RegistryType.ofKey(registry);
		return new TagKeyComponent<>(registry, r != null ? r.type() : TypeInfo.NONE);
	});

	@Override
	public String componentType() {
		return "tag_key";
	}

	@Override
	public TypeInfo typeInfo() {
		return registryType.shouldConvert() ? TAG_KEY_TYPE : TAG_KEY_TYPE.withParams(registryType);
	}

	@Override
	public JsonPrimitive write(KubeRecipe recipe, TagKey<T> value) {
		return new JsonPrimitive(value.location().toString());
	}

	@Override
	public TagKey<T> read(KubeRecipe recipe, Object from) {
		if (from instanceof TagKey<?> k) {
			return (TagKey<T>) k;
		}

		var s = from instanceof JsonPrimitive json ? json.getAsString() : String.valueOf(from);

		if (s.startsWith("#")) {
			s = s.substring(1);
		}

		return TagKey.create(registry, new ResourceLocation(s));
	}

	@Override
	public boolean hasPriority(KubeRecipe recipe, Object from) {
		return from instanceof TagKey<?> || (from instanceof CharSequence && from.toString().startsWith("#")) || (from instanceof JsonPrimitive json && json.isString() && json.getAsString().startsWith("#"));
	}

	@Override
	public String toString() {
		return componentType();
	}
}
