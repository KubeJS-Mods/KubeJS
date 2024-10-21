package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactory;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;

public record ResourceKeyComponent<T>(ResourceKey<? extends Registry<T>> registryKey) implements RecipeComponent<ResourceKey<T>> {
	public static final RecipeComponent<ResourceKey<Level>> DIMENSION = new ResourceKeyComponent<>(Registries.DIMENSION);
	public static final RecipeComponent<ResourceKey<LootTable>> LOOT_TABLE = new ResourceKeyComponent<>(Registries.LOOT_TABLE);

	@SuppressWarnings({"rawtypes"})
	public static final RecipeComponentFactory FACTORY = (registries, storage, reader) -> {
		reader.skipWhitespace();
		reader.expect('<');
		reader.skipWhitespace();
		var regId = ResourceLocation.read(reader);
		reader.expect('>');
		var key = ResourceKey.createRegistryKey(regId);
		return new ResourceKeyComponent(key);
	};

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
		var key = (ResourceKey) registryKey;

		if (key == Registries.DIMENSION) {
			return "dimension_resource_key";
		} else if (key == Registries.LOOT_TABLE) {
			return "loot_table_resource_key";
		} else {
			return "resource_key<" + registryKey.location() + ">";
		}
	}
}