package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.Nullable;

public record TagKeyComponent<T>(@Nullable ResourceKey<RecipeComponentType<?>> typeOverride, ResourceKey<? extends Registry<T>> registry, TypeInfo registryType, Codec<TagKey<T>> codec, TypeInfo typeInfo, boolean hashed) implements RecipeComponent<TagKey<T>> {
	public static final TypeInfo TAG_KEY_TYPE = TypeInfo.of(TagKey.class);

	public static final TagKeyComponent<Block> BLOCK = new TagKeyComponent<>(RecipeComponentType.builtin("block_tag"), Registries.BLOCK, TypeInfo.of(Block.class), false);
	public static final TagKeyComponent<Item> ITEM = new TagKeyComponent<>(RecipeComponentType.builtin("item_tag"), Registries.ITEM, TypeInfo.of(Item.class), false);
	public static final TagKeyComponent<EntityType<?>> ENTITY_TYPE = new TagKeyComponent<>(RecipeComponentType.builtin("entity_type_tag"), Registries.ENTITY_TYPE, TypeInfo.of(EntityType.class), false);
	public static final TagKeyComponent<Biome> BIOME = new TagKeyComponent<>(RecipeComponentType.builtin("biome_tag"), Registries.BIOME, TypeInfo.of(Biome.class), false);
	public static final TagKeyComponent<Fluid> FLUID = new TagKeyComponent<>(RecipeComponentType.builtin("fluid_tag"), Registries.FLUID, TypeInfo.of(Fluid.class), false);

	public static final TagKeyComponent<Block> HASHED_BLOCK = new TagKeyComponent<>(RecipeComponentType.builtin("hashed_block_tag"), Registries.BLOCK, TypeInfo.of(Block.class), true);
	public static final TagKeyComponent<Item> HASHED_ITEM = new TagKeyComponent<>(RecipeComponentType.builtin("hashed_item_tag"), Registries.ITEM, TypeInfo.of(Item.class), true);
	public static final TagKeyComponent<EntityType<?>> HASHED_ENTITY_TYPE = new TagKeyComponent<>(RecipeComponentType.builtin("hashed_entity_type_tag"), Registries.ENTITY_TYPE, TypeInfo.of(EntityType.class), true);
	public static final TagKeyComponent<Biome> HASHED_BIOME = new TagKeyComponent<>(RecipeComponentType.builtin("hashed_biome_tag"), Registries.BIOME, TypeInfo.of(Biome.class), true);
	public static final TagKeyComponent<Fluid> HASHED_FLUID = new TagKeyComponent<>(RecipeComponentType.builtin("hashed_fluid_tag"), Registries.FLUID, TypeInfo.of(Fluid.class), true);

	private static TagKeyComponent<?> of(ResourceKey<? extends Registry<?>> registry, boolean hashed) {
		var key = (ResourceKey) registry;

		if (key == Registries.BLOCK) {
			return hashed ? HASHED_BLOCK : BLOCK;
		} else if (key == Registries.ITEM) {
			return hashed ? HASHED_ITEM : ITEM;
		} else if (key == Registries.ENTITY_TYPE) {
			return hashed ? HASHED_ENTITY_TYPE : ENTITY_TYPE;
		} else if (key == Registries.BIOME) {
			return hashed ? HASHED_BIOME : BIOME;
		} else if (key == Registries.FLUID) {
			return hashed ? HASHED_FLUID : FLUID;
		} else {
			var r = RegistryType.ofKey(key);
			return new TagKeyComponent<>(null, key, r != null ? r.type() : TypeInfo.NONE, hashed);
		}
	}

	public static final ResourceKey<RecipeComponentType<?>> TYPE = RecipeComponentType.builtin("tag");
	public static final MapCodec<TagKeyComponent<?>> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		KubeJSCodecs.REGISTRY_KEY_CODEC.fieldOf("registry").forGetter(TagKeyComponent::registry),
		Codec.BOOL.fieldOf("hashed").orElse(false).forGetter(TagKeyComponent::hashed)
	).apply(instance, TagKeyComponent::of));

	public TagKeyComponent(@Nullable ResourceKey<RecipeComponentType<?>> typeOverride, ResourceKey<? extends Registry<T>> registry, TypeInfo registryType, boolean hashed) {
		this(typeOverride, registry, registryType, hashed ? TagKey.hashedCodec(registry) : TagKey.codec(registry), registryType.shouldConvert() ? TAG_KEY_TYPE : TAG_KEY_TYPE.withParams(registryType), hashed);
	}

	@Override
	public ResourceKey<RecipeComponentType<?>> type() {
		return typeOverride == null ? TYPE : typeOverride;
	}

	@Override
	public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
		return from instanceof TagKey<?> || (from instanceof CharSequence && from.toString().startsWith("#")) || (from instanceof JsonPrimitive json && json.isString() && json.getAsString().startsWith("#"));
	}

	@Override
	public TagKey<T> wrap(RecipeScriptContext cx, @Nullable Object from) {
		if (from instanceof TagKey<?> k) {
			return (TagKey<T>) k;
		}

		var s = from instanceof JsonPrimitive json ? json.getAsString() : String.valueOf(from);

		if (s.startsWith("#")) {
			s = s.substring(1);
		}

		return TagKey.create(registry, Identifier.parse(s));
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
			return (hashed ? "hashed_tag<" : "tag<") + ID.reduce(registry.identifier()) + ">";
		}
	}
}
