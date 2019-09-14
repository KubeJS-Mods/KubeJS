package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.documentation.DocumentationServer;
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
		event.add("documentation", DocumentationServer.INSTANCE);
		event.add("utils", new UtilsWrapper());
		event.add("uuid", new UUIDWrapper());
		event.add("json", new JsonWrapper());
		event.add("block", new BlockWrapper());
		event.add("item", new ItemWrapper());
		event.add("ingredient", new IngredientWrapper());
		event.add("nbt", new NBTWrapper());

		event.add("events", new ScriptEventsWrapper());
		event.add("text", new TextWrapper());
		event.add("oredict", new OreDictWrapper());
		event.add("fluid", new FluidWrapper());

		event.addConstant("EMPTY_ITEM", EmptyItemStackJS.INSTANCE);
		event.addConstant("SECOND", 1000L);
		event.addConstant("MINUTE", 60000L);
		event.addConstant("HOUR", 3600000L);

		event.addConstant("textColors", TextColor.MAP);

		for (TextColor color : TextColor.MAP.values())
		{
			event.addConstant(color.name(), color);
		}

		event.addConstant("SLOT_MAINHAND", EntityEquipmentSlot.MAINHAND);
		event.addConstant("SLOT_OFFHAND", EntityEquipmentSlot.OFFHAND);
		event.addConstant("SLOT_FEET", EntityEquipmentSlot.FEET);
		event.addConstant("SLOT_LEGS", EntityEquipmentSlot.LEGS);
		event.addConstant("SLOT_CHEST", EntityEquipmentSlot.CHEST);
		event.addConstant("SLOT_HEAD", EntityEquipmentSlot.HEAD);

		event.addConstant("RARITY_COMMON", EnumRarity.COMMON);
		event.addConstant("RARITY_UNCOMMON", EnumRarity.UNCOMMON);
		event.addConstant("RARITY_RARE", EnumRarity.RARE);
		event.addConstant("RARITY_EPIC", EnumRarity.EPIC);

		event.addConstant("AIR_ITEM", Items.AIR);
		event.addConstant("AIR_BLOCK", Blocks.AIR);

		event.addConstant("TOOL_TYPE_AXE", "axe");
		event.addConstant("TOOL_TYPE_PICKAXE", "pickaxe");
		event.addConstant("TOOL_TYPE_SHOVEL", "shovel");

		Map<String, EnumFacing> facingMap = new HashMap<>();

		for (EnumFacing facing : EnumFacing.VALUES)
		{
			event.addConstant(facing.getName().toUpperCase(), facing);
			facingMap.put(facing.getName(), facing);
		}

		event.addConstant("FACINGS", facingMap);

		event.addConstant("MAIN_HAND", EnumHand.MAIN_HAND);
		event.addConstant("OFF_HAND", EnumHand.OFF_HAND);
	}
}