package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.util.ID;
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
import org.jetbrains.annotations.Nullable;

public record TagKeyComponent<T>(@Nullable RecipeComponentType<?> typeOverride, ResourceKey<? extends Registry<T>> registry, TypeInfo registryType) implements RecipeComponent<TagKey<T>> {
	private static final TypeInfo TAG_KEY_TYPE = TypeInfo.of(TagKey.class);

	public static final RecipeComponentType<TagKey<Block>> BLOCK = RecipeComponentType.unit(KubeJS.id("block_tag"), type -> new TagKeyComponent<>(type, Registries.BLOCK, TypeInfo.of(Block.class)));
	public static final RecipeComponentType<TagKey<Item>> ITEM = RecipeComponentType.unit(KubeJS.id("item_tag"), type -> new TagKeyComponent<>(type, Registries.ITEM, TypeInfo.of(Item.class)));
	public static final RecipeComponentType<TagKey<EntityType<?>>> ENTITY_TYPE = RecipeComponentType.unit(KubeJS.id("entity_type_tag"), type -> new TagKeyComponent<>(type, Registries.ENTITY_TYPE, TypeInfo.of(EntityType.class)));
	public static final RecipeComponentType<TagKey<Biome>> BIOME = RecipeComponentType.unit(KubeJS.id("biome_tag"), type -> new TagKeyComponent<>(type, Registries.BIOME, TypeInfo.of(Biome.class)));
	public static final RecipeComponentType<TagKey<Fluid>> FLUID = RecipeComponentType.unit(KubeJS.id("fluid_tag"), type -> new TagKeyComponent<>(type, Registries.FLUID, TypeInfo.of(Fluid.class)));

	private static TagKeyComponent<?> of(ResourceKey<? extends Registry<?>> registry) {
		var key = (ResourceKey) registry;

		if (key == Registries.BLOCK) {
			return (TagKeyComponent<?>) BLOCK.instance();
		} else if (key == Registries.ITEM) {
			return (TagKeyComponent<?>) ITEM.instance();
		} else if (key == Registries.ENTITY_TYPE) {
			return (TagKeyComponent<?>) ENTITY_TYPE.instance();
		} else if (key == Registries.BIOME) {
			return (TagKeyComponent<?>) BIOME.instance();
		} else if (key == Registries.FLUID) {
			return (TagKeyComponent<?>) FLUID.instance();
		} else {
			var r = RegistryType.ofKey(registry);
			return new TagKeyComponent<>(null, key, r != null ? r.type() : TypeInfo.NONE);
		}
	}

	public static final RecipeComponentType<TagKeyComponent<?>> TYPE = RecipeComponentType.dynamic(KubeJS.id("tag"), RecordCodecBuilder.<TagKeyComponent<?>>mapCodec(instance -> instance.group(
		KubeJSCodecs.REGISTRY_KEY_CODEC.fieldOf("registry").forGetter(TagKeyComponent::registry)
	).apply(instance, TagKeyComponent::of)));

	@Override
	public RecipeComponentType<?> type() {
		return typeOverride == null ? TYPE : typeOverride;
	}

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
	public void buildUniqueId(UniqueIdBuilder builder, TagKey<T> value) {
		builder.append(value.location());
	}

	@Override
	public String toString() {
		return "tag<" + ID.reduce(registry.location()) + ">";
	}
}
