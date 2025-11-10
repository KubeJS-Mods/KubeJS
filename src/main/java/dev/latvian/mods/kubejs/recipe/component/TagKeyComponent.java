package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.util.ID;
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

public record TagKeyComponent<T>(@Nullable RecipeComponentType<?> typeOverride, ResourceKey<? extends Registry<T>> registry, TypeInfo registryType, Codec<TagKey<T>> codec, TypeInfo typeInfo, boolean hashed) implements RecipeComponent<TagKey<T>> {
	public static final TypeInfo TAG_KEY_TYPE = TypeInfo.of(TagKey.class);

	public static final RecipeComponentType<TagKey<Block>> BLOCK = RecipeComponentType.unit(KubeJS.id("block_tag"), type -> new TagKeyComponent<>(type, Registries.BLOCK, TypeInfo.of(Block.class), false));
	public static final RecipeComponentType<TagKey<Item>> ITEM = RecipeComponentType.unit(KubeJS.id("item_tag"), type -> new TagKeyComponent<>(type, Registries.ITEM, TypeInfo.of(Item.class), false));
	public static final RecipeComponentType<TagKey<EntityType<?>>> ENTITY_TYPE = RecipeComponentType.unit(KubeJS.id("entity_type_tag"), type -> new TagKeyComponent<>(type, Registries.ENTITY_TYPE, TypeInfo.of(EntityType.class), false));
	public static final RecipeComponentType<TagKey<Biome>> BIOME = RecipeComponentType.unit(KubeJS.id("biome_tag"), type -> new TagKeyComponent<>(type, Registries.BIOME, TypeInfo.of(Biome.class), false));
	public static final RecipeComponentType<TagKey<Fluid>> FLUID = RecipeComponentType.unit(KubeJS.id("fluid_tag"), type -> new TagKeyComponent<>(type, Registries.FLUID, TypeInfo.of(Fluid.class), false));

	public static final RecipeComponentType<TagKey<Block>> HASHED_BLOCK = RecipeComponentType.unit(KubeJS.id("hashed_block_tag"), type -> new TagKeyComponent<>(type, Registries.BLOCK, TypeInfo.of(Block.class), true));
	public static final RecipeComponentType<TagKey<Item>> HASHED_ITEM = RecipeComponentType.unit(KubeJS.id("hashed_item_tag"), type -> new TagKeyComponent<>(type, Registries.ITEM, TypeInfo.of(Item.class), true));
	public static final RecipeComponentType<TagKey<EntityType<?>>> HASHED_ENTITY_TYPE = RecipeComponentType.unit(KubeJS.id("hashed_entity_type_tag"), type -> new TagKeyComponent<>(type, Registries.ENTITY_TYPE, TypeInfo.of(EntityType.class), true));
	public static final RecipeComponentType<TagKey<Biome>> HASHED_BIOME = RecipeComponentType.unit(KubeJS.id("hashed_biome_tag"), type -> new TagKeyComponent<>(type, Registries.BIOME, TypeInfo.of(Biome.class), true));
	public static final RecipeComponentType<TagKey<Fluid>> HASHED_FLUID = RecipeComponentType.unit(KubeJS.id("hashed_fluid_tag"), type -> new TagKeyComponent<>(type, Registries.FLUID, TypeInfo.of(Fluid.class), true));

	private static TagKeyComponent<?> of(ResourceKey<? extends Registry<?>> registry, boolean hashed) {
		var key = (ResourceKey) registry;

		if (key == Registries.BLOCK) {
			return (TagKeyComponent<?>) (hashed ? HASHED_BLOCK : BLOCK).instance();
		} else if (key == Registries.ITEM) {
			return (TagKeyComponent<?>) (hashed ? HASHED_ITEM : ITEM).instance();
		} else if (key == Registries.ENTITY_TYPE) {
			return (TagKeyComponent<?>) (hashed ? HASHED_ENTITY_TYPE : ENTITY_TYPE).instance();
		} else if (key == Registries.BIOME) {
			return (TagKeyComponent<?>) (hashed ? HASHED_BIOME : BIOME).instance();
		} else if (key == Registries.FLUID) {
			return (TagKeyComponent<?>) (hashed ? HASHED_FLUID : FLUID).instance();
		} else {
			var r = RegistryType.ofKey(key);
			return new TagKeyComponent<>(null, key, r != null ? r.type() : TypeInfo.NONE, hashed);
		}
	}

	public static final RecipeComponentType<?> TYPE = RecipeComponentType.<TagKeyComponent<?>>dynamic(KubeJS.id("tag"), RecordCodecBuilder.mapCodec(instance -> instance.group(
		KubeJSCodecs.REGISTRY_KEY_CODEC.fieldOf("registry").forGetter(TagKeyComponent::registry),
		Codec.BOOL.fieldOf("hashed").orElse(false).forGetter(TagKeyComponent::hashed)
	).apply(instance, TagKeyComponent::of)));

	public TagKeyComponent(@Nullable RecipeComponentType<?> typeOverride, ResourceKey<? extends Registry<T>> registry, TypeInfo registryType, boolean hashed) {
		this(typeOverride, registry, registryType, hashed ? TagKey.hashedCodec(registry) : TagKey.codec(registry), registryType.shouldConvert() ? TAG_KEY_TYPE : TAG_KEY_TYPE.withParams(registryType), hashed);
	}

	@Override
	public RecipeComponentType<?> type() {
		return typeOverride == null ? TYPE : typeOverride;
	}

	@Override
	public boolean hasPriority(RecipeMatchContext cx, Object from) {
		return from instanceof TagKey<?> || (from instanceof CharSequence && from.toString().startsWith("#")) || (from instanceof JsonPrimitive json && json.isString() && json.getAsString().startsWith("#"));
	}

	@Override
	public TagKey<T> wrap(RecipeScriptContext cx, Object from) {
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
	public void buildUniqueId(UniqueIdBuilder builder, TagKey<T> value) {
		builder.append(value.location());
	}

	@Override
	public String toString() {
		if (typeOverride != null) {
			return typeOverride.toString();
		} else {
			return (hashed ? "hashed_tag<" : "tag<") + ID.reduce(registry.location()) + ">";
		}
	}
}
