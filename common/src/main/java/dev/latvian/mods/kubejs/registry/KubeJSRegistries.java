package dev.latvian.mods.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.material.Fluid;

import static net.minecraft.core.Registry.*;

public class KubeJSRegistries {
	@Info("Platform-agnostic wrapper of minecraft registries, can be used to register content or get objects from the registry")
	public static final Registries REGISTRIES = Registries.get(KubeJS.MOD_ID);

	@Info("Gets the registry associated with the id")
	public static <T> Registrar<T> byId(ResourceLocation id) {
		return genericRegistry(ResourceKey.createRegistryKey(id));
	}

	@HideFromJS
	public static <T> Registrar<T> genericRegistry(ResourceKey<Registry<T>> key) {
		return REGISTRIES.get(key);
	}

	@Info("Gets the block registrar")
	public static Registrar<Block> blocks() {
		return genericRegistry(BLOCK_REGISTRY);
	}

	@Info("Gets the attribute registrar")
	public static Registrar<Attribute> attributes() {
		return genericRegistry(ATTRIBUTE_REGISTRY);
	}

	@Info("Gets the block entity registrar")
	public static Registrar<BlockEntityType<?>> blockEntities() {
		return genericRegistry(BLOCK_ENTITY_TYPE_REGISTRY);
	}

	@Info("Gets the item registrar")
	public static Registrar<Item> items() {
		return genericRegistry(ITEM_REGISTRY);
	}

	@Info("Gets the fluid registrar")
	public static Registrar<Fluid> fluids() {
		return genericRegistry(FLUID_REGISTRY);
	}

	@Info("Gets the entity type registrar")
	public static Registrar<EntityType<?>> entityTypes() {
		return genericRegistry(ENTITY_TYPE_REGISTRY);
	}

	@Info("Gets the menu type registrar")
	public static Registrar<MenuType<?>> menuTypes() {
		return genericRegistry(MENU_REGISTRY);
	}

	@Info("Gets the sound event registrar")
	public static Registrar<SoundEvent> soundEvents() {
		return genericRegistry(SOUND_EVENT_REGISTRY);
	}

	@Info("Gets the recipe serializer registrar")
	public static Registrar<RecipeSerializer<?>> recipeSerializers() {
		return genericRegistry(RECIPE_SERIALIZER_REGISTRY);
	}

	@Info("Gets the biome registrar")
	public static Registrar<Biome> biomes() {
		return genericRegistry(BIOME_REGISTRY);
	}

	@Info("Gets the chunk generator registrar")
	public static Registrar<Codec<? extends ChunkGenerator>> chunkGenerators() {
		return genericRegistry(CHUNK_GENERATOR_REGISTRY);
	}

	@Info("Gets the block entity registrar")
	public static Registrar<BlockEntityType<?>> blockEntityTypes() {
		return genericRegistry(BLOCK_ENTITY_TYPE_REGISTRY);
	}

	@Info("Gets the potion registrar")
	public static Registrar<Potion> potions() {
		return genericRegistry(POTION_REGISTRY);
	}

	@Info("Gets the enchantment registrar")
	public static Registrar<Enchantment> enchantments() {
		return genericRegistry(ENCHANTMENT_REGISTRY);
	}

	@Info("Gets the mob effect registrar")
	public static Registrar<MobEffect> mobEffects() {
		return genericRegistry(MOB_EFFECT_REGISTRY);
	}

	@Info("Gets the particle type registrar")
	public static Registrar<ParticleType<?>> particleTypes() {
		return genericRegistry(PARTICLE_TYPE_REGISTRY);
	}
}
