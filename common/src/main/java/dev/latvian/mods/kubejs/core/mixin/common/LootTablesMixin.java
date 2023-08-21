package dev.latvian.mods.kubejs.core.mixin.common;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.core.LootTablesKJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(LootDataManager.class)
public abstract class LootTablesMixin implements LootTablesKJS {

	@Shadow
	private Map<LootDataId<?>, ?> elements;

	// TODO: (low priority) Replace with a less destructive mixin type
	@Inject(method = "apply*", at = @At("RETURN"))
	private void kjs$apply(Map<LootDataType<?>, Map<ResourceLocation, ?>> parsedMap, CallbackInfo ci) {
		kjs$completeReload(parsedMap, elements);
	}

	@SuppressWarnings({"UnresolvedMixinReference", "DefaultAnnotationParam"})
	@Inject(method = {"method_51189", "lambda$scheduleElementParse$5", "m_278660_"}, remap = false,
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/server/packs/resources/SimpleJsonResourceReloadListener;scanDirectory(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/lang/String;Lcom/google/gson/Gson;Ljava/util/Map;)V",
			shift = At.Shift.AFTER,
			remap = true
		), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void kjs$readLootTableJsons(ResourceManager rm, LootDataType type, Map map0, CallbackInfo ci, Map<ResourceLocation, JsonElement> map) {
		if (type.equals(LootDataType.TABLE)) {
			LootTablesKJS.kjs$postLootEvents(map);
		}
	}
}
