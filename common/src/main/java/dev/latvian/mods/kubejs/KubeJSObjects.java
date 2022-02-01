package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.DetectorInstance;
import dev.latvian.mods.kubejs.enchantment.EnchantmentBuilder;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.util.BuilderBase;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class KubeJSObjects {
	public static final List<BuilderBase> ALL = new ArrayList<>();
	public static final Map<ResourceLocation, ItemBuilder> ITEMS = new LinkedHashMap<>();
	public static final Map<ResourceLocation, BlockBuilder> BLOCKS = new LinkedHashMap<>();
	public static final Map<ResourceLocation, FluidBuilder> FLUIDS = new LinkedHashMap<>();
	public static final Map<ResourceLocation, EnchantmentBuilder> ENCHANTMENTS = new LinkedHashMap<>();
	public static final Map<String, DetectorInstance> DETECTORS = new LinkedHashMap<>();

	public static void register() {
		ALL.clear();
		ITEMS.clear();
		BLOCKS.clear();
		FLUIDS.clear();
		ENCHANTMENTS.clear();
	}
}