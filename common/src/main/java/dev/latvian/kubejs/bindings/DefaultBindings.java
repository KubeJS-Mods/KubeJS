package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.event.IEventHandler;
import dev.latvian.kubejs.fluid.FluidWrapper;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.PlatformWrapper;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerSettings;
import dev.latvian.kubejs.text.TextColor;
import dev.latvian.kubejs.util.ListJS;
import me.shedaniel.architectury.registry.ToolType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;

import java.util.HashMap;

/**
 * @author LatvianModder
 */
public class DefaultBindings {
	public static final HashMap<String, Object> GLOBAL = new HashMap<>();

	public static void init(ScriptManager manager, BindingsEvent event) {
		event.add("global", GLOBAL);

		if (event.type == ScriptType.SERVER) {
			ServerSettings.instance = new ServerSettings();
			event.add("settings", ServerSettings.instance);
		}

		event.add("Platform", PlatformWrapper.getInstance());
		event.add("mod", PlatformWrapper.getInstance());
		event.add("console", manager.type.console);
		event.add("events", new ScriptEventsWrapper(event.type.manager.get().events));

		event.addFunction("onEvent", args -> {
			for (Object o : ListJS.orSelf(args[0])) {
				event.type.manager.get().events.listen(String.valueOf(o), (IEventHandler) args[1]);
			}

			return null;
		}, null, IEventHandler.class);

		event.add("Utils", new UtilsWrapper());
		event.add("utils", new UtilsWrapper());
		event.add("Text", new TextWrapper());
		event.add("text", new TextWrapper());
		event.add("uuid", new UUIDWrapper());
		event.add("json", new JsonWrapper());
		event.add("Block", new BlockWrapper());
		event.add("block", new BlockWrapper());
		event.add("Item", new ItemWrapper());
		event.add("item", new ItemWrapper());
		event.add("Ingredient", new IngredientWrapper());
		event.add("ingredient", new IngredientWrapper());
		event.add("nbt", new NBTWrapper());
		event.add("facing", new FacingWrapper());

		event.add("Fluid", new FluidWrapper());
		event.add("fluid", new FluidWrapper());

		event.addConstant("SECOND", 1000L);
		event.addConstant("MINUTE", 60000L);
		event.addConstant("HOUR", 3600000L);

		event.add("TextColor", TextColor.class);

		for (TextColor color : TextColor.MAP.values()) {
			event.addConstant(color.name.toUpperCase(), color);
		}

		event.add("EquipmentSlot", EquipmentSlot.class);
		event.addConstant("SLOT_MAINHAND", EquipmentSlot.MAINHAND);
		event.addConstant("SLOT_OFFHAND", EquipmentSlot.OFFHAND);
		event.addConstant("SLOT_FEET", EquipmentSlot.FEET);
		event.addConstant("SLOT_LEGS", EquipmentSlot.LEGS);
		event.addConstant("SLOT_CHEST", EquipmentSlot.CHEST);
		event.addConstant("SLOT_HEAD", EquipmentSlot.HEAD);

		event.add("Rarity", RarityWrapper.class);
		event.addConstant("RARITY_COMMON", RarityWrapper.COMMON);
		event.addConstant("RARITY_UNCOMMON", RarityWrapper.UNCOMMON);
		event.addConstant("RARITY_RARE", RarityWrapper.RARE);
		event.addConstant("RARITY_EPIC", RarityWrapper.EPIC);

		event.addConstant("AIR_ITEM", Items.AIR);
		event.addConstant("AIR_BLOCK", Blocks.AIR);

		event.add("ToolType", ToolType.class);
		event.addConstant("TOOL_TYPE_AXE", ToolType.AXE);
		event.addConstant("TOOL_TYPE_PICKAXE", ToolType.PICKAXE);
		event.addConstant("TOOL_TYPE_SHOVEL", ToolType.SHOVEL);
		event.addConstant("TOOL_TYPE_HOE", ToolType.HOE);

		event.add("Hand", InteractionHand.class);
		event.addConstant("MAIN_HAND", InteractionHand.MAIN_HAND);
		event.addConstant("OFF_HAND", InteractionHand.OFF_HAND);

		event.add("DecorationGenerationStep", GenerationStep.Decoration.class);
		event.add("CarvingGenerationStep", GenerationStep.Carving.class);

		KubeJS.instance.proxy.clientBindings(event);
	}
}