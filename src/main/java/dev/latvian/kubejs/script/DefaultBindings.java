package dev.latvian.kubejs.script;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSBindingsEvent;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.event.ScriptEventsWrapper;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.OreDictUtils;
import dev.latvian.kubejs.text.TextColor;
import dev.latvian.kubejs.text.TextUtilsJS;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.LoggerWrapperJS;
import dev.latvian.kubejs.util.UUIDUtilsJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;

/**
 * @author LatvianModder
 */
public class DefaultBindings
{
	public static void init(KubeJSBindingsEvent event)
	{
		event.add("mod", new ScriptModData());
		event.add("log", new LoggerWrapperJS(KubeJS.LOGGER));
		event.add("utils", UtilsJS.INSTANCE);
		event.add("uuid", UUIDUtilsJS.INSTANCE);
		event.add("json", JsonUtilsJS.INSTANCE);

		event.add("events", new ScriptEventsWrapper());
		event.add("text", TextUtilsJS.INSTANCE);
		event.add("oredict", OreDictUtils.INSTANCE);
		event.add("materials", MaterialListJS.INSTANCE.map);

		event.add("EMPTY_ITEM", ItemStackJS.EMPTY);
		event.add("SECOND", 1000L);
		event.add("MINUTE", 60000L);
		event.add("HOUR", 3600000L);

		event.add("textColors", TextColor.MAP);

		for (TextColor color : TextColor.MAP.values())
		{
			event.add(color.name(), color);
		}

		event.add("SLOT_MAINHAND", EntityEquipmentSlot.MAINHAND);
		event.add("SLOT_OFFHAND", EntityEquipmentSlot.OFFHAND);
		event.add("SLOT_FEET", EntityEquipmentSlot.FEET);
		event.add("SLOT_LEGS", EntityEquipmentSlot.LEGS);
		event.add("SLOT_CHEST", EntityEquipmentSlot.CHEST);
		event.add("SLOT_HEAD", EntityEquipmentSlot.HEAD);

		event.add("RARITY_COMMON", EnumRarity.COMMON);
		event.add("RARITY_UNCOMMON", EnumRarity.UNCOMMON);
		event.add("RARITY_RARE", EnumRarity.RARE);
		event.add("RARITY_EPIC", EnumRarity.EPIC);

		event.add("AIR_ITEM", Items.AIR);
		event.add("AIR_BLOCK", Blocks.AIR);

		event.add("TOOL_TYPE_AXE", "axe");
		event.add("TOOL_TYPE_PICKAXE", "pickaxe");
		event.add("TOOL_TYPE_SHOVEL", "shovel");
	}
}