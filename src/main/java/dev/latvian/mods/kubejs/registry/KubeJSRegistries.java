package dev.latvian.mods.kubejs.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.ArmorMaterial;

public interface KubeJSRegistries {
	ResourceKey<Registry<ArmorMaterial>> ARMOR_MATERIAL = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("kubejs", "armor_material"));
}
