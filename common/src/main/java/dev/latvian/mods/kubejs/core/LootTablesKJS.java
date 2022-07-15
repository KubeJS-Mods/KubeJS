package dev.latvian.mods.kubejs.core;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.loot.BlockLootEventJS;
import dev.latvian.mods.kubejs.loot.ChestLootEventJS;
import dev.latvian.mods.kubejs.loot.EntityLootEventJS;
import dev.latvian.mods.kubejs.loot.FishingLootEventJS;
import dev.latvian.mods.kubejs.loot.GenericLootEventJS;
import dev.latvian.mods.kubejs.loot.GiftLootEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.server.ServerSettings;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author LatvianModder
 */
public interface LootTablesKJS {
	default void applyKJS0(Map<ResourceLocation, JsonElement> map, BiConsumer<ResourceLocation, JsonElement> action) {
		Map<ResourceLocation, JsonElement> map1 = new HashMap<>(map);
		KubeJSEvents.SERVER_GENERIC_LOOT_TABLES.post(new GenericLootEventJS(map1));
		KubeJSEvents.SERVER_BLOCK_LOOT_TABLES.post(new BlockLootEventJS(map1));
		KubeJSEvents.SERVER_ENTITY_LOOT_TABLES.post(new EntityLootEventJS(map1));
		KubeJSEvents.SERVER_GIFT_LOOT_TABLES.post(new GiftLootEventJS(map1));
		KubeJSEvents.SERVER_FISHING_LOOT_TABLES.post(new FishingLootEventJS(map1));
		KubeJSEvents.SERVER_CHEST_LOOT_TABLES.post(new ChestLootEventJS(map1));

		for (var entry : map1.entrySet()) {
			try {
				action.accept(entry.getKey(), entry.getValue());
			} catch (Exception ex) {
				ConsoleJS.SERVER.error("Failed to load loot table " + entry.getKey() + ": " + ex + "\nJson: " + entry.getValue());
			}
		}

		ServerSettings.exportData();

		if (CommonProperties.get().announceReload && ServerJS.instance != null && !CommonProperties.get().hideServerScriptErrors) {
			if (ScriptType.SERVER.errors.isEmpty()) {
				ServerJS.instance.tell(Component.literal("Reloaded with no KubeJS errors!").withStyle(ChatFormatting.GREEN));
			} else {
				ServerJS.instance.tell(Component.literal("KubeJS errors found [" + ScriptType.SERVER.errors.size() + "]! Run ")
						.append(Component.literal("'/kubejs errors'")
								.click(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kubejs errors")))
						.hover(Component.literal("Click to run"))
						.append(Component.literal(" for more info"))
						.withStyle(ChatFormatting.DARK_RED)
				);
			}
		}
	}
}
