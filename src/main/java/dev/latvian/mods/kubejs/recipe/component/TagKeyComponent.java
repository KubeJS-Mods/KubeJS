package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactory;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.rhino.Context;
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

	public static final RecipeComponentFactory FACTORY = (storage, reader) -> {
		reader.skipWhitespace();
		reader.expect('<');
		reader.skipWhitespace();
		var registry = ResourceKey.createRegistryKey(ResourceLocation.read(reader));
		reader.expect('>');

		var r = RegistryType.ofKey(registry);
		return new TagKeyComponent<>(registry, r != null ? r.type() : TypeInfo.NONE);
	};

	@Override
	public Codec<TagKey<T>> codec() {
		return TagKey.codec(registry);
	}

	@Override
	public TypeInfo typeInfo() {
		return registryType.shouldConvert() ? TAG_KEY_TYPE : TAG_KEY_TYPE.withParams(registryType);
	}

	@Override
	public TagKey<T> wrap(Context cx, KubeRecipe recipe, Object from) {
		if (from instanceof TagKey<?> k) {
			return (TagKey<T>) k;
		}

		var s = from instanceof JsonPrimitive json ? json.getAsString() : String.valueOf(from);

		if (s.startsWith("#")) {
			s = s.substring(1);
		}

		return TagKey.create(registry, ResourceLocation.parse(s));
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof TagKey<?> || (from instanceof CharSequence && from.toString().startsWith("#")) || (from instanceof JsonPrimitive json && json.isString() && json.getAsString().startsWith("#"));
	}

	@Override
	public String toString() {
		var key = (ResourceKey) registry;

		if (key == Registries.BLOCK) {
			return "block_tag";
		} else if (key == Registries.ITEM) {
			return "item_tag";
		} else if (key == Registries.ENTITY_TYPE) {
			return "entity_type_tag";
		} else if (key == Registries.BIOME) {
			return "biome_tag";
		} else if (key == Registries.FLUID) {
			return "fluid_tag";
		} else {
			return "tag<" + key.location() + ">";
		}
	}
}
