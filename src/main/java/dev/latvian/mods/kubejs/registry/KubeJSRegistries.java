package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.neoforged.neoforge.registries.RegistryBuilder;

public interface KubeJSRegistries {
	ResourceKey<Registry<ArmorMaterial>> ARMOR_MATERIAL = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("kubejs", "armor_material"));

	ResourceKey<Registry<RecipeComponentType<?>>> RECIPE_COMPONENT_TYPE = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("kubejs", "recipe_component_type"));

	Registry<RecipeComponentType<?>> RECIPE_COMPONENT_TYPE_REGISTRY = new RegistryBuilder<>(RECIPE_COMPONENT_TYPE)
		.disableRegistrationCheck()
		.create();
}
