package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LootDataManager.class)
public abstract class LootTablesMixin {

	@Shadow
	private Map<LootDataId<?>, ?> elements;

	@Inject(method = "apply*", at = @At("RETURN"))
	private void kjs$apply(Map<LootDataType<?>, Map<ResourceLocation, ?>> parsedMap, CallbackInfo ci) {
		if (DataExport.export != null) {
			// part 2: add loot tables to export
			for (var entry : elements.entrySet()) {
				var type = entry.getKey().type();
				var id = entry.getKey().location();
				try {
					var lootJson = UtilsJS.toJsonOrThrow(UtilsJS.cast(entry.getValue()), type.codec);
					var fileName = "%s/%s/%s.json".formatted(type.directory(), id.getNamespace(), id.getPath());

					DataExport.export.addJson(fileName, lootJson);
				} catch (Exception ex) {
					ConsoleJS.SERVER.error("Failed to export loot table %s as JSON!".formatted(id), ex);
				}
			}
		}
	}
}
