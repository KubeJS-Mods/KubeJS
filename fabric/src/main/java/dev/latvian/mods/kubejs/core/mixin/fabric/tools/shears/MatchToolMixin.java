package dev.latvian.mods.kubejs.core.mixin.fabric.tools.shears;

import dev.latvian.mods.kubejs.item.custom.ShearsItemBuilder;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(MatchTool.class)
public abstract class MatchToolMixin {

	@Shadow
	@Final
	ItemPredicate predicate;

	@Inject(at = @At("RETURN"), method = "<init>")
	private void kjs$injectCustomShears(CallbackInfo ci) {
		if (predicate.items != null && predicate.items.contains(Items.SHEARS)) {
			var set = new HashSet<>(predicate.items);
			for (var e : RegistryInfo.ITEM.objects.entrySet()) {
				if (e.getValue() instanceof ShearsItemBuilder builder) {
					set.add(builder.get());
				}
			}
			predicate.items = Set.copyOf(set);
		}
	}
}