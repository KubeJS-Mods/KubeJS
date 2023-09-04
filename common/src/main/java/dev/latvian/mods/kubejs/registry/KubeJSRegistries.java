package dev.latvian.mods.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.material.Fluid;

public class KubeJSRegistries {
	@Info("Platform-agnostic wrapper of minecraft registries, can be used to register content or get objects from the registry")
	public static final RegistrarManager REGISTRIES = RegistrarManager.get(KubeJS.MOD_ID);

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
		return genericRegistry(Registries.BLOCK);
	}

	@Info("Gets the attribute registrar")
	public static Registrar<Attribute> attributes() {
		return genericRegistry(Registries.ATTRIBUTE);
	}

	@Info("Gets the block entity registrar")
	public static Registrar<BlockEntityType<?>> blockEntities() {
		return genericRegistry(Registries.BLOCK_ENTITY_TYPE);
	}

	@Info("Gets the item registrar")
	public static Registrar<Item> items() {
		return genericRegistry(Registries.ITEM);
	}

	@Info("Gets the fluid registrar")
	public static Registrar<Fluid> fluids() {
		return genericRegistry(Registries.FLUID);
	}

	@Info("Gets the entity type registrar")
	public static Registrar<EntityType<?>> entityTypes() {
		return genericRegistry(Registries.ENTITY_TYPE);
	}

	@Info("Gets the menu type registrar")
	public static Registrar<MenuType<?>> menuTypes() {
		return genericRegistry(Registries.MENU);
	}

	@Info("Gets the sound event registrar")
	public static Registrar<SoundEvent> soundEvents() {
		return genericRegistry(Registries.SOUND_EVENT);
	}

	@Info("Gets the recipe serializer registrar")
	public static Registrar<RecipeSerializer<?>> recipeSerializers() {
		return genericRegistry(Registries.RECIPE_SERIALIZER);
	}

	@Info("Gets the biome registrar")
	public static Registrar<Biome> biomes() {
		return genericRegistry(Registries.BIOME);
	}

	@Info("Gets the chunk generator registrar")
	public static Registrar<Codec<? extends ChunkGenerator>> chunkGenerators() {
		return genericRegistry(Registries.CHUNK_GENERATOR);
	}

	@Info("Gets the block entity registrar")
	public static Registrar<BlockEntityType<?>> blockEntityTypes() {
		return genericRegistry(Registries.BLOCK_ENTITY_TYPE);
	}

	@Info("Gets the potion registrar")
	public static Registrar<Potion> potions() {
		return genericRegistry(Registries.POTION);
	}

	@Info("Gets the enchantment registrar")
	public static Registrar<Enchantment> enchantments() {
		return genericRegistry(Registries.ENCHANTMENT);
	}

	@Info("Gets the mob effect registrar")
	public static Registrar<MobEffect> mobEffects() {
		return genericRegistry(Registries.MOB_EFFECT);
	}

	@Info("Gets the creative tab registrar")
	public static Registrar<CreativeModeTab> creativeModeTabs() {
		return genericRegistry(Registries.CREATIVE_MODE_TAB);
	}
}
