package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.EnchantmentKJS;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin implements EnchantmentKJS {
	@Shadow
	@Final
	private Holder.Reference<Enchantment> builtInRegistryHolder;

	@Unique
	private ResourceKey<Enchantment> kjs$registryKey;

	@Unique
	private String kjs$id;

	@Override
	public Holder<Enchantment> kjs$asHolder() {
		return builtInRegistryHolder;
	}

	@Override
	public ResourceKey<Enchantment> kjs$getRegistryKey() {
		if (kjs$registryKey == null) {
			kjs$registryKey = EnchantmentKJS.super.kjs$getRegistryKey();
		}

		return kjs$registryKey;
	}

	@Override
	public String kjs$getId() {
		if (kjs$id == null) {
			kjs$id = EnchantmentKJS.super.kjs$getId();
		}

		return kjs$id;
	}
}
