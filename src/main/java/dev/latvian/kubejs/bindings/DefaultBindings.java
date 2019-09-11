package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.event.ScriptEventsWrapper;
import dev.latvian.kubejs.fluid.FluidWrapper;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptModData;
import dev.latvian.kubejs.text.TextColor;
import dev.latvian.kubejs.util.LoggerWrapperJS;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.Loader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class DefaultBindings
{
	public static void init(ScriptManager manager, BindingsEvent event)
	{
		event.add("mod", new ScriptModData("forge", "1.12.2", Loader.instance().getIndexedModList().keySet()));
		event.add("log", new LoggerWrapperJS(KubeJS.LOGGER));
		event.add("runtime", manager.runtime);
		event.add("utils", new UtilsWrapper());
		event.add("uuid", new UUIDWrapper());
		event.add("json", new JsonWrapper());
		event.add("item", new ItemWrapper());
		event.add("ingredient", new IngredientWrapper());
		event.add("nbt", new NBTWrapper());

		event.add("events", new ScriptEventsWrapper());
		event.add("text", new TextWrapper());
		event.add("oredict", new OreDictWrapper());
		event.add("materials", MaterialListJS.INSTANCE.map);
		event.add("fluid", new FluidWrapper());

		event.add("EMPTY_ITEM", EmptyItemStackJS.INSTANCE);
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

		Map<String, EnumFacing> facingMap = new HashMap<>();

		for (EnumFacing facing : EnumFacing.VALUES)
		{
			event.add(facing.getName().toUpperCase(), facing);
			facingMap.put(facing.getName(), facing);
		}

		event.add("FACINGS", facingMap);

		event.add("MAIN_HAND", EnumHand.MAIN_HAND);
		event.add("OFF_HAND", EnumHand.OFF_HAND);
	}
}