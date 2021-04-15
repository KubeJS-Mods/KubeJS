package dev.latvian.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.bindings.BlockWrapper;
import dev.latvian.kubejs.bindings.FacingWrapper;
import dev.latvian.kubejs.bindings.IngredientWrapper;
import dev.latvian.kubejs.bindings.ItemWrapper;
import dev.latvian.kubejs.bindings.JsonWrapper;
import dev.latvian.kubejs.bindings.NBTWrapper;
import dev.latvian.kubejs.bindings.RarityWrapper;
import dev.latvian.kubejs.bindings.ScriptEventsWrapper;
import dev.latvian.kubejs.bindings.TextWrapper;
import dev.latvian.kubejs.bindings.UUIDWrapper;
import dev.latvian.kubejs.bindings.UtilsWrapper;
import dev.latvian.kubejs.block.BlockStatePredicate;
import dev.latvian.kubejs.block.MaterialJS;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.event.IEventHandler;
import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.fluid.FluidWrapper;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.PlatformWrapper;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerSettings;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextColor;
import dev.latvian.kubejs.util.ClassList;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UUIDUtilsJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import me.shedaniel.architectury.registry.Registry;
import me.shedaniel.architectury.registry.ToolType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class BuiltinKubeJSPlugin implements KubeJSPlugin {
	public static final HashMap<String, Object> GLOBAL = new HashMap<>();

	@Override
	public void addClasses(ScriptType type, ClassList list) {
		list.deny("java.lang");
		list.deny("java.io"); // IO and network
		list.deny("java.nio");
		list.deny("java.net");
		list.deny("sun");
		list.deny("com.sun");
		list.deny("io.netty");

		list.allow("java.util");
		list.deny("java.util.jar");
		list.deny("java.util.zip");
		list.allow("it.unimi.dsi.fastutil"); // FastUtil

		list.deny("dev.latvian.mods.rhino"); // Rhino itself
		list.deny("dev.latvian.kubejs.script"); // KubeJS itself

		list.allow("net.minecraft");
		list.allow("com.mojang.authlib.GameProfile");
		list.allow("com.mojang.util.UUIDTypeAdapter");

		list.allow("net.minecraftforge"); // Forge / FML internal stuff
		list.deny("cpw.mods.modlauncher");
		list.deny("cpw.mods.gross");
		list.deny("net.minecraftforge.fml");
		list.deny("net.minecraftforge.accesstransformer");
		list.deny("net.minecraftforge.coremod");
		list.deny("org.openjdk.nashorn");
		list.deny("jdk.nashorn");

		list.allow("net.fabricmc"); // Fabric internal stuff
		list.deny("net.fabricmc.accesswidener");
		list.deny("net.fabricmc.devlaunchinjector");
		list.deny("net.fabricmc.loader");
		list.deny("net.fabricmc.tinyremapper");

		list.deny("org.objectweb.asm"); // ASM
		list.deny("org.spongepowered.asm"); // Sponge ASM
		list.deny("me.shedaniel.architectury"); // Architectury

		list.deny("com.chocohead.mm"); // Manningham Mills

		list.allow("dev.latvian.kubejs");
		list.deny("dev.latvian.kubejs.mixin");
	}

	@Override
	public void addBindings(BindingsEvent event) {
		event.add("global", GLOBAL);

		if (event.type == ScriptType.SERVER) {
			ServerSettings.instance = new ServerSettings();
			event.add("settings", ServerSettings.instance);
		}

		event.add("Platform", PlatformWrapper.getInstance());
		event.add("mod", PlatformWrapper.getInstance());
		event.add("console", event.type.console);
		event.add("events", new ScriptEventsWrapper(event.type.manager.get().events));

		event.addFunction("onEvent", args -> onEvent(event, args), null, IEventHandler.class);

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
		event.add("NBT", new NBTWrapper());
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

		KubeJS.PROXY.clientBindings(event);
	}

	private static Object onEvent(BindingsEvent event, Object[] args) {
		for (Object o : ListJS.orSelf(args[0])) {
			event.type.manager.get().events.listen(String.valueOf(o), (IEventHandler) args[1]);
		}

		return null;
	}

	@Override
	public void addTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
		// Java / Minecraft //
		typeWrappers.register(String.class, String::valueOf);
		typeWrappers.register(CharSequence.class, String::valueOf);
		typeWrappers.register(ResourceLocation.class, o -> UtilsJS.getMCID(o == null ? null : o.toString()));
		typeWrappers.register(JsonObject.class, MapJS::json);
		typeWrappers.register(JsonArray.class, ListJS::json);
		typeWrappers.register(ItemStack.class, o -> ItemStackJS.of(o).getItemStack());
		typeWrappers.register(CompoundTag.class, MapJS::nbt);
		typeWrappers.register(CollectionTag.class, ListJS::nbt);
		typeWrappers.register(ListTag.class, o -> (ListTag) ListJS.nbt(o));
		typeWrappers.register(UUID.class, UUIDUtilsJS::fromString);
		typeWrappers.register(Pattern.class, UtilsJS::parseRegex);
		typeWrappers.register(Component.class, Text::componentOfObject);
		typeWrappers.register(MutableComponent.class, o -> new TextComponent("").append(Text.componentOfObject(o)));
		typeWrappers.register(BlockPos.class, o -> {
			if (o instanceof BlockPos) {
				return (BlockPos) o;
			} else if (o instanceof List && ((List<?>) o).size() >= 3) {
				return new BlockPos(((Number) ((List<?>) o).get(0)).intValue(), ((Number) ((List<?>) o).get(1)).intValue(), ((Number) ((List<?>) o).get(2)).intValue());
			}

			return BlockPos.ZERO;
		});

		typeWrappers.register(Item.class, o -> ItemStackJS.of(o).getItem());
		wrapRegistry(typeWrappers, Block.class, KubeJSRegistries.blocks());
		wrapRegistry(typeWrappers, Fluid.class, KubeJSRegistries.fluids());
		wrapRegistry(typeWrappers, SoundEvent.class, KubeJSRegistries.soundEvents());

		// KubeJS //
		typeWrappers.register(MapJS.class, MapJS::of);
		typeWrappers.register(ListJS.class, ListJS::of);
		typeWrappers.register(ItemStackJS.class, ItemStackJS::of);
		typeWrappers.register(IngredientJS.class, IngredientJS::of);
		typeWrappers.register(IngredientStackJS.class, o -> IngredientJS.of(o).asIngredientStack());
		typeWrappers.register(Text.class, Text::of);
		typeWrappers.register(BlockStatePredicate.class, BlockStatePredicate::of);
		typeWrappers.register(FluidStackJS.class, FluidStackJS::of);
		typeWrappers.register(RecipeFilter.class, RecipeFilter::of);
		typeWrappers.register(MaterialJS.class, MaterialListJS.INSTANCE::of);
	}

	private static <T> void wrapRegistry(TypeWrappers typeWrappers, Class<T> c, Registry<T> registry) {
		typeWrappers.register(c, o -> {
			if (o == null) {
				return null;
			} else if (c.isAssignableFrom(o.getClass())) {
				return (T) o;
			}

			return registry.get(new ResourceLocation(o.toString()));
		});
	}
}
