package dev.latvian.mods.kubejs.core.mixin.fabric.tools.shears;

import dev.latvian.mods.kubejs.item.custom.ShearsItemBuilder;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mixin(MatchTool.class)
public abstract class MatchToolMixin {

	@Shadow
	@Final
	@Mutable
	private Optional<ItemPredicate> predicate;

	@Inject(at = @At("RETURN"), method = "<init>")
	private void kjs$injectCustomShears(CallbackInfo ci) {
		if (predicate.isEmpty()) {
			return;
		}
		var original = this.predicate.get();
		// TODO: I hope this can just be removed at some point
		var items = original.items();
		if (items.isPresent()) {
			var set = new ArrayList<Holder<Item>>();
			for (var e : RegistryInfo.ITEM.objects.entrySet()) {
				if (e.getValue() instanceof ShearsItemBuilder builder) {
					set.add(RegistryInfo.ITEM.getHolder(e.getKey()));
				}
			}
			items.get().forEach(set::add);
			this.predicate = Optional.of(new ItemPredicate(original.tag(), Optional.of(HolderSet.direct(set)), original.count(), original.durability(),
				original.enchantments(), original.storedEnchantments(), original.potion(), original.nbt()));
		}
	}
}