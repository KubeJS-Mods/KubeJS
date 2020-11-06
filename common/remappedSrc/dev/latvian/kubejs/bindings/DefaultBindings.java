package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.fluid.FluidWrapper;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptModData;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerSettings;
import dev.latvian.kubejs.text.TextColor;
import dev.latvian.kubejs.util.MapJS;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolType;

/**
 * @author LatvianModder
 */
public class DefaultBindings
{
	public static final MapJS GLOBAL = new MapJS();

	public static void init(ScriptManager manager, BindingsEvent event)
	{
		event.add("global", GLOBAL);

		if (event.type == ScriptType.SERVER)
		{
			ServerSettings.instance = new ServerSettings();
			event.add("settings", ServerSettings.instance);
		}

		event.add("mod", ScriptModData.getInstance());
		event.add("console", manager.type.console);
		event.add("events", new ScriptEventsWrapper(event.type.manager.get().events));
		event.add("utils", new UtilsWrapper());
		event.add("text", new TextWrapper());
		event.add("uuid", new UUIDWrapper());
		event.add("json", new JsonWrapper());
		event.add("block", new BlockWrapper());
		event.add("item", new ItemWrapper());
		event.add("ingredient", new IngredientWrapper());
		event.add("nbt", new NBTWrapper());
		event.add("facing", new FacingWrapper());

		event.add("fluid", new FluidWrapper());

		event.addConstant("SECOND", 1000L);
		event.addConstant("MINUTE", 60000L);
		event.addConstant("HOUR", 3600000L);

		for (TextColor color : TextColor.MAP.values())
		{
			event.addConstant(color.name.toUpperCase(), color);
		}

		event.addConstant("SLOT_MAINHAND", EquipmentSlot.MAINHAND);
		event.addConstant("SLOT_OFFHAND", EquipmentSlot.OFFHAND);
		event.addConstant("SLOT_FEET", EquipmentSlot.FEET);
		event.addConstant("SLOT_LEGS", EquipmentSlot.LEGS);
		event.addConstant("SLOT_CHEST", EquipmentSlot.CHEST);
		event.addConstant("SLOT_HEAD", EquipmentSlot.HEAD);

		event.addConstant("RARITY_COMMON", Rarity.COMMON);
		event.addConstant("RARITY_UNCOMMON", Rarity.UNCOMMON);
		event.addConstant("RARITY_RARE", Rarity.RARE);
		event.addConstant("RARITY_EPIC", Rarity.EPIC);

		event.addConstant("AIR_ITEM", Items.AIR);
		event.addConstant("AIR_BLOCK", Blocks.AIR);

		event.addConstant("TOOL_TYPE_AXE", ToolType.AXE);
		event.addConstant("TOOL_TYPE_PICKAXE", ToolType.PICKAXE);
		event.addConstant("TOOL_TYPE_SHOVEL", ToolType.SHOVEL);

		event.addConstant("MAIN_HAND", InteractionHand.MAIN_HAND);
		event.addConstant("OFF_HAND", InteractionHand.OFF_HAND);

		KubeJS.instance.proxy.clientBindings(event);
	}
}