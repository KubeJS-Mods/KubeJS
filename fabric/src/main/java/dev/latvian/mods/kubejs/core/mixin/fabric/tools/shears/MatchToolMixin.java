package dev.latvian.mods.kubejs.core.mixin.fabric.tools.shears;

import dev.latvian.mods.kubejs.item.custom.ShearsItemBuilder;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MatchTool.class)
public abstract class MatchToolMixin {
	@ModifyVariable(at = @At("HEAD"), method = "<init>", argsOnly = true)
	private static ItemPredicate shearsLoot(ItemPredicate predicate) {
		var element = predicate.serializeToJson().deepCopy();
		var object = element.isJsonObject() ? element.getAsJsonObject() : null;

		if (object != null) {
			var items = object.getAsJsonArray("items");

			if (items != null) {
				for (var jsonElement : items.deepCopy()) {
					if (jsonElement.getAsString().equals("minecraft:shears")) {
						for (var e : RegistryInfo.ITEM.objects.entrySet()) {
							if (e.getValue() instanceof ShearsItemBuilder) {
								items.add(e.getKey().toString());
							}
						}
					}
				}
			}
		}

		return ItemPredicate.fromJson(element);
	}
}