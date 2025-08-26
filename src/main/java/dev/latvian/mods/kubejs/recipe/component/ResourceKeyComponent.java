package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

public record ResourceKeyComponent<T>(@Nullable RecipeComponentType<?> typeOverride, ResourceKey<? extends Registry<T>> registryKey, Codec<ResourceKey<T>> codec, TypeInfo typeInfo) implements RecipeComponent<ResourceKey<T>> {
	private static <T> ResourceKeyComponent<T> create(@Nullable RecipeComponentType<?> typeOverride, ResourceKey<? extends Registry<T>> registryKey) {
		var reg = RegistryType.ofKey(registryKey);
		return new ResourceKeyComponent<>(typeOverride, registryKey, ResourceKey.codec(registryKey), reg == null ? TypeInfo.of(ResourceKey.class) : TypeInfo.of(ResourceKey.class).withParams(reg.type()));
	}

	public static final RecipeComponentType<ResourceKey<Level>> DIMENSION = RecipeComponentType.unit(KubeJS.id("dimension_resource_key"), type -> create(type, Registries.DIMENSION));
	public static final RecipeComponentType<ResourceKey<LootTable>> LOOT_TABLE = RecipeComponentType.unit(KubeJS.id("loot_table_resource_key"), type -> create(type, Registries.LOOT_TABLE));

	private static ResourceKeyComponent<?> of(ResourceKey key) {
		if (key == Registries.DIMENSION) {
			return (ResourceKeyComponent<?>) DIMENSION.instance();
		} else if (key == Registries.LOOT_TABLE) {
			return (ResourceKeyComponent<?>) LOOT_TABLE.instance();
		} else {
			return create(null, key);
		}
	}

	public static final RecipeComponentType<ResourceKeyComponent<?>> TYPE = RecipeComponentType.dynamic(KubeJS.id("resource_key"), RecordCodecBuilder.<ResourceKeyComponent<?>>mapCodec(instance -> instance.group(
		KubeJSCodecs.REGISTRY_KEY_CODEC.fieldOf("registry").forGetter(ResourceKeyComponent::registryKey)
	).apply(instance, ResourceKeyComponent::of)));

	@Override
	public RecipeComponentType<?> type() {
		return typeOverride == null ? TYPE : typeOverride;
	}

	@Override
	public ResourceKey<T> wrap(Context cx, KubeRecipe recipe, Object from) {
		return ResourceKey.create(registryKey, ID.mc(from));
	}

	@Override
	public String toString() {
		if (typeOverride != null) {
			return typeOverride.toString();
		} else {
			return "resource_key<" + ID.reduce(registryKey.location()) + ">";
		}
	}

	@Override
	public String toString(OpsContainer ops, ResourceKey<T> value) {
		return value.location().toString();
	}
}