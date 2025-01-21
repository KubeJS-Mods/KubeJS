package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.rhino.Undefined;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public record MapColorHelper(int id, String name, MapColor color, Vector3f rgb) implements Function<BlockState, MapColor> {
	public static final Map<String, MapColorHelper> NAME_MAP = new HashMap<>(64);
	public static final Map<Integer, MapColorHelper> ID_MAP = new HashMap<>(64);

	private static MapColorHelper add(String id, MapColor color) {
		float r = (color.col >> 16 & 0xFF) / 255F;
		float g = (color.col >> 8 & 0xFF) / 255F;
		float b = (color.col & 0xFF) / 255F;
		var helper = new MapColorHelper(color.id, id, color, new Vector3f(r, g, b));
		NAME_MAP.put(id, helper);
		ID_MAP.put(color.id, helper);
		return helper;
	}

	public static final MapColorHelper NONE = add("none", MapColor.NONE);

	static {
		add("grass", MapColor.GRASS);
		add("sand", MapColor.SAND);
		add("wool", MapColor.WOOL);
		add("fire", MapColor.FIRE);
		add("ice", MapColor.ICE);
		add("metal", MapColor.METAL);
		add("plant", MapColor.PLANT);
		add("snow", MapColor.SNOW);
		add("clay", MapColor.CLAY);
		add("dirt", MapColor.DIRT);
		add("stone", MapColor.STONE);
		add("water", MapColor.WATER);
		add("wood", MapColor.WOOD);
		add("quartz", MapColor.QUARTZ);
		add("color_orange", MapColor.COLOR_ORANGE);
		add("color_magenta", MapColor.COLOR_MAGENTA);
		add("color_light_blue", MapColor.COLOR_LIGHT_BLUE);
		add("color_yellow", MapColor.COLOR_YELLOW);
		add("color_light_green", MapColor.COLOR_LIGHT_GREEN);
		add("color_pink", MapColor.COLOR_PINK);
		add("color_gray", MapColor.COLOR_GRAY);
		add("color_light_gray", MapColor.COLOR_LIGHT_GRAY);
		add("color_cyan", MapColor.COLOR_CYAN);
		add("color_purple", MapColor.COLOR_PURPLE);
		add("color_blue", MapColor.COLOR_BLUE);
		add("color_brown", MapColor.COLOR_BROWN);
		add("color_green", MapColor.COLOR_GREEN);
		add("color_red", MapColor.COLOR_RED);
		add("color_black", MapColor.COLOR_BLACK);
		add("gold", MapColor.GOLD);
		add("diamond", MapColor.DIAMOND);
		add("lapis", MapColor.LAPIS);
		add("emerald", MapColor.EMERALD);
		add("podzol", MapColor.PODZOL);
		add("nether", MapColor.NETHER);
		add("terracotta_white", MapColor.TERRACOTTA_WHITE);
		add("terracotta_orange", MapColor.TERRACOTTA_ORANGE);
		add("terracotta_magenta", MapColor.TERRACOTTA_MAGENTA);
		add("terracotta_light_blue", MapColor.TERRACOTTA_LIGHT_BLUE);
		add("terracotta_yellow", MapColor.TERRACOTTA_YELLOW);
		add("terracotta_light_green", MapColor.TERRACOTTA_LIGHT_GREEN);
		add("terracotta_pink", MapColor.TERRACOTTA_PINK);
		add("terracotta_gray", MapColor.TERRACOTTA_GRAY);
		add("terracotta_light_gray", MapColor.TERRACOTTA_LIGHT_GRAY);
		add("terracotta_cyan", MapColor.TERRACOTTA_CYAN);
		add("terracotta_purple", MapColor.TERRACOTTA_PURPLE);
		add("terracotta_blue", MapColor.TERRACOTTA_BLUE);
		add("terracotta_brown", MapColor.TERRACOTTA_BROWN);
		add("terracotta_green", MapColor.TERRACOTTA_GREEN);
		add("terracotta_red", MapColor.TERRACOTTA_RED);
		add("terracotta_black", MapColor.TERRACOTTA_BLACK);
		add("crimson_nylium", MapColor.CRIMSON_NYLIUM);
		add("crimson_stem", MapColor.CRIMSON_STEM);
		add("crimson_hyphae", MapColor.CRIMSON_HYPHAE);
		add("warped_nylium", MapColor.WARPED_NYLIUM);
		add("warped_stem", MapColor.WARPED_STEM);
		add("warped_hyphae", MapColor.WARPED_HYPHAE);
		add("warped_wart_block", MapColor.WARPED_WART_BLOCK);
		add("deepslate", MapColor.DEEPSLATE);
		add("raw_iron", MapColor.RAW_IRON);
		add("glow_lichen", MapColor.GLOW_LICHEN);
	}

	public static MapColor wrap(Object o) {
		if (o == null || Undefined.isUndefined(o)) {
			return MapColor.NONE;
		} else if (o instanceof MapColor c) {
			return c;
		} else if (o instanceof CharSequence s) {
			if (s.isEmpty()) {
				return MapColor.NONE;
			} else if (s.charAt(0) == '#') {
				return findClosest(Integer.decode(s.toString())).color;
			} else {
				return NAME_MAP.getOrDefault(s.toString().toLowerCase(Locale.ROOT), NONE).color;
			}
		} else if (o instanceof Number n) {
			return findClosest(n.intValue()).color;
		} else if (o instanceof DyeColor c) {
			return c.getMapColor();
		}

		return MapColor.NONE;
	}

	public static MapColorHelper reverse(MapColor c) {
		return ID_MAP.getOrDefault(c.id, NONE);
	}

	public static MapColorHelper findClosest(int rgbi) {
		var rgb = new Vector3f((rgbi >> 16 & 0xFF) / 255F, (rgbi >> 8 & 0xFF) / 255F, (rgbi & 0xFF) / 255F);
		MapColorHelper closest = null;
		var lastDist = Float.MAX_VALUE;

		for (var helper : NAME_MAP.values()) {
			if (helper.color != MapColor.NONE) {
				var dist = helper.rgb.distanceSquared(rgb);

				if (dist < lastDist) {
					closest = helper;
					lastDist = dist;
				}
			}
		}

		return closest == null ? NONE : closest;
	}

	@Override
	public MapColor apply(BlockState blockState) {
		return color;
	}
}
