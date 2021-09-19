package dev.latvian.kubejs.core;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.CommonProperties;
import dev.latvian.kubejs.loot.BlockLootEventJS;
import dev.latvian.kubejs.loot.EntityLootEventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.server.ServerSettings;
import dev.latvian.kubejs.util.ConsoleJS;
import net.minecraft.ChatFormatting;
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
		new BlockLootEventJS(map1).post(ScriptType.SERVER, "block.loot_tables");
		new EntityLootEventJS(map1).post(ScriptType.SERVER, "entity.loot_tables");

		for (Map.Entry<ResourceLocation, JsonElement> entry : map1.entrySet()) {
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
				ServerJS.instance.tell(new TextComponent("KubeJS errors found [" + ScriptType.SERVER.errors.size() + "]! Run '/kubejs errors' for more info").withStyle(ChatFormatting.DARK_RED));
			}
		}
	}
}
