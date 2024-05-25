package dev.latvian.mods.kubejs.core.mixin;

import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LootDataType.class)
public abstract class LootDataTypeMixin<T> {
	@Shadow
	@Final
	private String directory;

	@Inject(method = "deserialize", at = @At("RETURN"))
	private <V> void kjs$exportLootTable(ResourceLocation id, DynamicOps<V> dynamicOps, V object, CallbackInfoReturnable<Optional<T>> cir) {
		if (DataExport.export != null && object instanceof JsonObject json) {
			try {
				var fileName = "%s/%s/%s.json".formatted(directory, id.getNamespace(), id.getPath());
				DataExport.export.addJson(fileName, json);
			} catch (Exception ex) {
				ConsoleJS.SERVER.error("Failed to export loot table %s as JSON!".formatted(id), ex);
			}
		}
	}
}
