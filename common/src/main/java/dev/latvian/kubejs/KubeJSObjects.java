package dev.latvian.kubejs;

import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.block.DetectorInstance;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.util.BuilderBase;
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
	public static final Map<String, DetectorInstance> DETECTORS = new LinkedHashMap<>();

	public static void register() {
		ALL.clear();
		ITEMS.clear();
		BLOCKS.clear();
		FLUIDS.clear();
	}
}