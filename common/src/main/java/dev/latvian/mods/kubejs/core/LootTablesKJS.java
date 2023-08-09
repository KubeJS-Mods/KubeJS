package dev.latvian.mods.kubejs.core;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.loot.*;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataType;

import java.util.Map;

public interface LootTablesKJS {
	static void kjs$postLootEvents(Map<ResourceLocation, JsonElement> map) {
		// part 1: modifying loot tables
		// TODO (low): We can now also modify the parsed loot tables directly in the apply method (see parsedMap in kjs$completeReload below)
		ServerEvents.GENERIC_LOOT_TABLES.post(ScriptType.SERVER, new GenericLootEventJS(map));
		ServerEvents.BLOCK_LOOT_TABLES.post(ScriptType.SERVER, new BlockLootEventJS(map));
		ServerEvents.ENTITY_LOOT_TABLES.post(ScriptType.SERVER, new EntityLootEventJS(map));
		ServerEvents.GIFT_LOOT_TABLES.post(ScriptType.SERVER, new GiftLootEventJS(map));
		ServerEvents.FISHING_LOOT_TABLES.post(ScriptType.SERVER, new FishingLootEventJS(map));
		ServerEvents.CHEST_LOOT_TABLES.post(ScriptType.SERVER, new ChestLootEventJS(map));
	}

	default void kjs$completeReload(Map<LootDataType<?>, Map<ResourceLocation, ?>> parsedMap, Map<LootDataId<?>, ?> elements) {
		// TODO: choose which of these maps we want to use for the data export
		if (DataExport.export != null) {
			// part 2: add loot tables to export
			for (var entry : elements.entrySet()) {
				var type = entry.getKey().type();
				var id = entry.getKey().location();
				try {
					var lootJson = type.parser().toJsonTree(entry.getValue());
					var fileName = "%s/%s/%s.json".formatted(type.directory(), id.getNamespace(), id.getPath());

					DataExport.export.addJson(fileName, lootJson);
				} catch (Exception ex) {
					ConsoleJS.SERVER.error("Failed to export loot table %s as JSON!".formatted(id), ex);
				}
			}
		}

		// part 3: export data
		DataExport.exportData();

		// part 4: complete reload
		if (UtilsJS.staticServer != null && CommonProperties.get().announceReload && !CommonProperties.get().hideServerScriptErrors) {
			if (ScriptType.SERVER.errors.isEmpty()) {
				UtilsJS.staticServer.kjs$tell(Component.literal("Reloaded with no KubeJS errors!").withStyle(ChatFormatting.GREEN));
			} else {
				UtilsJS.staticServer.kjs$tell(ScriptType.SERVER.errorsComponent("/kubejs errors"));
			}
		}
	}
}
