package dev.latvian.mods.kubejs.core;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.CommonProperties;
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
import net.minecraft.network.chat.TextComponent;
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
		new GenericLootEventJS(map1).post(ScriptType.SERVER, "generic.loot_tables");
		new BlockLootEventJS(map1).post(ScriptType.SERVER, "block.loot_tables");
		new EntityLootEventJS(map1).post(ScriptType.SERVER, "entity.loot_tables");
		new GiftLootEventJS(map1).post(ScriptType.SERVER, "gift.loot_tables");
		new FishingLootEventJS(map1).post(ScriptType.SERVER, "fishing.loot_tables");
		new ChestLootEventJS(map1).post(ScriptType.SERVER, "chest.loot_tables");

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
				ServerJS.instance.tell(new TextComponent("Reloaded with no KubeJS errors!").withStyle(ChatFormatting.GREEN));
			} else {
				ServerJS.instance.tell(new TextComponent("KubeJS errors found [" + ScriptType.SERVER.errors.size() + "]! Run ")
						.append(new TextComponent("'/kubejs errors'")
								.click(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "kubejs errors")))
								.hover(new TextComponent("Click to run"))
						.append(new TextComponent(" for more info"))
						.withStyle(ChatFormatting.DARK_RED)
				);
			}
		}
	}
}
