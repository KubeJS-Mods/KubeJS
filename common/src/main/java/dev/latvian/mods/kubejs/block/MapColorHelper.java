package dev.latvian.mods.kubejs.block;

import com.mojang.math.Vector3f;
import dev.latvian.mods.rhino.Undefined;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public record MapColorHelper(int id, String name, MaterialColor color, Vector3f rgb) implements Function<BlockState, MaterialColor> {
	public static final Map<String, MapColorHelper> NAME_MAP = new HashMap<>(64);
	public static final Map<Integer, MapColorHelper> ID_MAP = new HashMap<>(64);

	private static MapColorHelper add(String id, MaterialColor color) {
		float r = (color.col >> 16 & 0xFF) / 255F;
		float g = (color.col >> 8 & 0xFF) / 255F;
		float b = (color.col & 0xFF) / 255F;
		var helper = new MapColorHelper(color.id, id, color, new Vector3f(r, g, b));
		NAME_MAP.put(id, helper);
		ID_MAP.put(color.id, helper);
		return helper;
	}

	public static final MapColorHelper NONE = add("none", MaterialColor.NONE);

	static {
		add("grass", MaterialColor.GRASS);
		add("sand", MaterialColor.SAND);
		add("wool", MaterialColor.WOOL);
		add("fire", MaterialColor.FIRE);
		add("ice", MaterialColor.ICE);
		add("metal", MaterialColor.METAL);
		add("plant", MaterialColor.PLANT);
		add("snow", MaterialColor.SNOW);
		add("clay", MaterialColor.CLAY);
		add("dirt", MaterialColor.DIRT);
		add("stone", MaterialColor.STONE);
		add("water", MaterialColor.WATER);
		add("wood", MaterialColor.WOOD);
		add("quartz", MaterialColor.QUARTZ);
		add("color_orange", MaterialColor.COLOR_ORANGE);
		add("color_magenta", MaterialColor.COLOR_MAGENTA);
		add("color_light_blue", MaterialColor.COLOR_LIGHT_BLUE);
		add("color_yellow", MaterialColor.COLOR_YELLOW);
		add("color_light_green", MaterialColor.COLOR_LIGHT_GREEN);
		add("color_pink", MaterialColor.COLOR_PINK);
		add("color_gray", MaterialColor.COLOR_GRAY);
		add("color_light_gray", MaterialColor.COLOR_LIGHT_GRAY);
		add("color_cyan", MaterialColor.COLOR_CYAN);
		add("color_purple", MaterialColor.COLOR_PURPLE);
		add("color_blue", MaterialColor.COLOR_BLUE);
		add("color_brown", MaterialColor.COLOR_BROWN);
		add("color_green", MaterialColor.COLOR_GREEN);
		add("color_red", MaterialColor.COLOR_RED);
		add("color_black", MaterialColor.COLOR_BLACK);
		add("gold", MaterialColor.GOLD);
		add("diamond", MaterialColor.DIAMOND);
		add("lapis", MaterialColor.LAPIS);
		add("emerald", MaterialColor.EMERALD);
		add("podzol", MaterialColor.PODZOL);
		add("nether", MaterialColor.NETHER);
		add("terracotta_white", MaterialColor.TERRACOTTA_WHITE);
		add("terracotta_orange", MaterialColor.TERRACOTTA_ORANGE);
		add("terracotta_magenta", MaterialColor.TERRACOTTA_MAGENTA);
		add("terracotta_light_blue", MaterialColor.TERRACOTTA_LIGHT_BLUE);
		add("terracotta_yellow", MaterialColor.TERRACOTTA_YELLOW);
		add("terracotta_light_green", MaterialColor.TERRACOTTA_LIGHT_GREEN);
		add("terracotta_pink", MaterialColor.TERRACOTTA_PINK);
		add("terracotta_gray", MaterialColor.TERRACOTTA_GRAY);
		add("terracotta_light_gray", MaterialColor.TERRACOTTA_LIGHT_GRAY);
		add("terracotta_cyan", MaterialColor.TERRACOTTA_CYAN);
		add("terracotta_purple", MaterialColor.TERRACOTTA_PURPLE);
		add("terracotta_blue", MaterialColor.TERRACOTTA_BLUE);
		add("terracotta_brown", MaterialColor.TERRACOTTA_BROWN);
		add("terracotta_green", MaterialColor.TERRACOTTA_GREEN);
		add("terracotta_red", MaterialColor.TERRACOTTA_RED);
		add("terracotta_black", MaterialColor.TERRACOTTA_BLACK);
		add("crimson_nylium", MaterialColor.CRIMSON_NYLIUM);
		add("crimson_stem", MaterialColor.CRIMSON_STEM);
		add("crimson_hyphae", MaterialColor.CRIMSON_HYPHAE);
		add("warped_nylium", MaterialColor.WARPED_NYLIUM);
		add("warped_stem", MaterialColor.WARPED_STEM);
		add("warped_hyphae", MaterialColor.WARPED_HYPHAE);
		add("warped_wart_block", MaterialColor.WARPED_WART_BLOCK);
		add("deepslate", MaterialColor.DEEPSLATE);
		add("raw_iron", MaterialColor.RAW_IRON);
		add("glow_lichen", MaterialColor.GLOW_LICHEN);
	}

	public static MaterialColor of(Object o) {
		if (o == null || Undefined.isUndefined(o)) {
			return MaterialColor.NONE;
		} else if (o instanceof MaterialColor c) {
			return c;
		} else if (o instanceof CharSequence s) {
			if (s.isEmpty()) {
				return MaterialColor.NONE;
			} else if (s.charAt(0) == '#') {
				return findClosest(Integer.decode(s.toString())).color;
			} else {
				return NAME_MAP.getOrDefault(s.toString().toLowerCase(), NONE).color;
			}
		} else if (o instanceof Number n) {
			return findClosest(n.intValue()).color;
		} else if (o instanceof DyeColor c) {
			return c.getMaterialColor();
		}

		return MaterialColor.NONE;
	}

	public static MapColorHelper reverse(MaterialColor c) {
		return ID_MAP.getOrDefault(c.id, NONE);
	}

	public static MapColorHelper findClosest(int rgbi) {
		var rgb = new Vector3f((rgbi >> 16 & 0xFF) / 255F, (rgbi >> 8 & 0xFF) / 255F, (rgbi & 0xFF) / 255F);
		MapColorHelper closest = null;
		var lastDist = Float.MAX_VALUE;

		for (var helper : NAME_MAP.values()) {
			if (helper.color != MaterialColor.NONE) {
				var dist = distSq(helper.rgb, rgb);

				if (dist < lastDist) {
					closest = helper;
					lastDist = dist;
				}
			}
		}

		return closest == null ? NONE : closest;
	}

	private static float distSq(Vector3f a, Vector3f b) {
		return (a.x() - b.x()) * (a.x() - b.x()) + (a.y() - b.y()) * (a.y() - b.y()) + (a.z() - b.z()) * (a.z() - b.z());
	}

	@Override
	public MaterialColor apply(BlockState blockState) {
		return color;
	}
}
