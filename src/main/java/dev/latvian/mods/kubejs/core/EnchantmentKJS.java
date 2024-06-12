package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.item.enchantment.Enchantment;

@RemapPrefixForJS("kjs$")
public interface EnchantmentKJS extends RegistryObjectKJS<Enchantment> {
	@Override
	default RegistryInfo<Enchantment> kjs$getKubeRegistry() {
		return RegistryInfo.ENCHANTMENT;
	}
}
