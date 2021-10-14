package dev.latvian.kubejs;

import com.mojang.serialization.Codec;
import dev.architectury.architectury.registry.Registries;
import dev.architectury.architectury.registry.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.material.Fluid;

import static net.minecraft.core.Registry.*;

public class KubeJSRegistries {
	private static final Registries REGISTRIES = Registries.get(KubeJS.MOD_ID);

	public static <T> Registry<T> genericRegistry(ResourceKey<net.minecraft.core.Registry<T>> key) {
		return REGISTRIES.get(key);
	}

	public static Registry<net.minecraft.core.Registry<?>> registries() {
		return genericRegistry(ResourceKey.createRegistryKey(new ResourceLocation("root")));
	}

	public static Registry<Block> blocks() {
		return genericRegistry(BLOCK_REGISTRY);
	}

	public static Registry<BlockEntityType<?>> blockEntities() {
		return genericRegistry(BLOCK_ENTITY_TYPE_REGISTRY);
	}

	public static Registry<Item> items() {
		return genericRegistry(ITEM_REGISTRY);
	}

	public static Registry<Fluid> fluids() {
		return genericRegistry(FLUID_REGISTRY);
	}

	public static Registry<EntityType<?>> entityTypes() {
		return genericRegistry(ENTITY_TYPE_REGISTRY);
	}

	public static Registry<SoundEvent> soundEvents() {
		return genericRegistry(SOUND_EVENT_REGISTRY);
	}

	public static Registry<RecipeSerializer<?>> recipeSerializers() {
		return genericRegistry(RECIPE_SERIALIZER_REGISTRY);
	}

	public static Registry<Codec<? extends ChunkGenerator>> chunkGenerators() {
		return genericRegistry(CHUNK_GENERATOR_REGISTRY);
	}

	public static Registry<BlockEntityType<?>> blockEntityTypes() {
		return genericRegistry(BLOCK_ENTITY_TYPE_REGISTRY);
	}

	public static Registry<Potion> potions() {
		return genericRegistry(POTION_REGISTRY);
	}

	public static Registry<Enchantment> enchantments() {
		return genericRegistry(ENCHANTMENT_REGISTRY);
	}

	public static Registry<MobEffect> mobEffects() {
		return genericRegistry(MOB_EFFECT_REGISTRY);
	}
}
