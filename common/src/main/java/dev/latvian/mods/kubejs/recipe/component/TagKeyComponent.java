package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public record TagKeyComponent<T>(ResourceKey<? extends Registry<T>> registry, Class<?> registryType) implements RecipeComponent<TagKey<T>> {
	public static final RecipeComponent<TagKey<Block>> BLOCK = new TagKeyComponent<>(Registries.BLOCK, Block.class);
	public static final RecipeComponent<TagKey<Item>> ITEM = new TagKeyComponent<>(Registries.ITEM, Item.class);
	public static final RecipeComponent<TagKey<EntityType<?>>> ENTITY_TYPE = new TagKeyComponent<>(Registries.ENTITY_TYPE, EntityType.class);
	public static final RecipeComponent<TagKey<Biome>> BIOME = new TagKeyComponent<>(Registries.BIOME, Biome.class);

	@Override
	public String componentType() {
		return "tag_key";
	}

	@Override
	public Class<?> componentClass() {
		return TagKey.class;
	}

	@Override
	public TypeDescJS constructorDescription(DescriptionContext ctx) {
		return TypeDescJS.STRING.or(ctx.javaType(TagKey.class).withGenerics(ctx.javaType(registryType)));
	}

	@Override
	public JsonPrimitive write(RecipeJS recipe, TagKey<T> value) {
		return new JsonPrimitive(value.location().toString());
	}

	@Override
	public TagKey<T> read(RecipeJS recipe, Object from) {
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
	public boolean hasPriority(RecipeJS recipe, Object from) {
		return from instanceof TagKey<?> || (from instanceof CharSequence && from.toString().startsWith("#")) || (from instanceof JsonPrimitive json && json.isString() && json.getAsString().startsWith("#"));
	}

	@Override
	public String toString() {
		return componentType();
	}
}
