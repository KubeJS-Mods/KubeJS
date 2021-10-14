package dev.latvian.kubejs;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import net.minecraft.core.Registry;
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

	public static <T> Registrar<T> genericRegistry(ResourceKey<net.minecraft.core.Registry<T>> key) {
		return REGISTRIES.get(key);
	}

	public static Registrar<Registry<?>> registries() {
		return genericRegistry(ResourceKey.createRegistryKey(new ResourceLocation("root")));
	}

	public static Registrar<Block> blocks() {
		return genericRegistry(BLOCK_REGISTRY);
	}

	public static Registrar<BlockEntityType<?>> blockEntities() {
		return genericRegistry(BLOCK_ENTITY_TYPE_REGISTRY);
	}

	public static Registrar<Item> items() {
		return genericRegistry(ITEM_REGISTRY);
	}

	public static Registrar<Fluid> fluids() {
		return genericRegistry(FLUID_REGISTRY);
	}

	public static Registrar<EntityType<?>> entityTypes() {
		return genericRegistry(ENTITY_TYPE_REGISTRY);
	}

	public static Registrar<SoundEvent> soundEvents() {
		return genericRegistry(SOUND_EVENT_REGISTRY);
	}

	public static Registrar<RecipeSerializer<?>> recipeSerializers() {
		return genericRegistry(RECIPE_SERIALIZER_REGISTRY);
	}

	public static Registrar<Codec<? extends ChunkGenerator>> chunkGenerators() {
		return genericRegistry(CHUNK_GENERATOR_REGISTRY);
	}

	public static Registrar<BlockEntityType<?>> blockEntityTypes() {
		return genericRegistry(BLOCK_ENTITY_TYPE_REGISTRY);
	}

	public static Registrar<Potion> potions() {
		return genericRegistry(POTION_REGISTRY);
	}

	public static Registrar<Enchantment> enchantments() {
		return genericRegistry(ENCHANTMENT_REGISTRY);
	}

	public static Registrar<MobEffect> mobEffects() {
		return genericRegistry(MOB_EFFECT_REGISTRY);
	}
}
