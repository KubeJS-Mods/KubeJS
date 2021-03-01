package dev.latvian.kubejs;

import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.architectury.registry.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import static net.minecraft.core.Registry.*;

public class KubeJSRegistries {
	private static final Registries REGISTRIES = Registries.get(KubeJS.MOD_ID);

	public static Registry<Block> blocks() {
		return REGISTRIES.get(BLOCK_REGISTRY);
	}

	public static Registry<Item> items() {
		return REGISTRIES.get(ITEM_REGISTRY);
	}

	public static Registry<Fluid> fluids() {
		return REGISTRIES.get(FLUID_REGISTRY);
	}

	public static Registry<EntityType<?>> entityTypes() {
		return REGISTRIES.get(ENTITY_TYPE_REGISTRY);
	}

	public static Registry<SoundEvent> soundEvents() {
		return REGISTRIES.get(SOUND_EVENT_REGISTRY);
	}

	public static Registry<RecipeSerializer<?>> recipeSerializers() {
		return REGISTRIES.get(RECIPE_SERIALIZER_REGISTRY);
	}
}
