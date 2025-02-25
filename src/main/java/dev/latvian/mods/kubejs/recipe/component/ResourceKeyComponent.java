package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

public record ResourceKeyComponent<T>(@Nullable RecipeComponentType<?> typeOverride, ResourceKey<? extends Registry<T>> registryKey) implements RecipeComponent<ResourceKey<T>> {
	public static final RecipeComponentType<ResourceKey<Level>> DIMENSION = RecipeComponentType.unit(KubeJS.id("dimension_resource_key"), type -> new ResourceKeyComponent<>(type, Registries.DIMENSION));
	public static final RecipeComponentType<ResourceKey<LootTable>> LOOT_TABLE = RecipeComponentType.unit(KubeJS.id("loot_table_resource_key"), type -> new ResourceKeyComponent<>(type, Registries.LOOT_TABLE));

	private static ResourceKeyComponent<?> of(ResourceKey key) {
		if (key == Registries.DIMENSION) {
			return (ResourceKeyComponent<?>) DIMENSION.instance();
		} else if (key == Registries.LOOT_TABLE) {
			return (ResourceKeyComponent<?>) LOOT_TABLE.instance();
		} else {
			return new ResourceKeyComponent(null, key);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static final RecipeComponentType<ResourceKeyComponent<?>> TYPE = RecipeComponentType.dynamic(KubeJS.id("resource_key"), RecordCodecBuilder.<ResourceKeyComponent<?>>mapCodec(instance -> instance.group(
		KubeJSCodecs.REGISTRY_KEY_CODEC.fieldOf("registry").forGetter(ResourceKeyComponent::registryKey)
	).apply(instance, ResourceKeyComponent::of)));

	@Override
	public RecipeComponentType<?> type() {
		return typeOverride == null ? TYPE : typeOverride;
	}

	@Override
	public Codec<ResourceKey<T>> codec() {
		return ResourceKey.codec(registryKey);
	}

	@Override
	public TypeInfo typeInfo() {
		var reg = RegistryType.ofKey(registryKey);
		return reg == null ? TypeInfo.of(ResourceKey.class) : TypeInfo.of(ResourceKey.class).withParams(reg.type());
	}

	@Override
	public ResourceKey<T> wrap(Context cx, KubeRecipe recipe, Object from) {
		return ResourceKey.create(registryKey, ID.mc(from));
	}

	@Override
	public String toString() {
		return "resource_key<" + registryKey.location() + ">";
	}
}