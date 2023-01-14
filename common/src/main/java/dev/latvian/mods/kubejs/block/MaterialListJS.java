package dev.latvian.mods.kubejs.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class MaterialListJS {
	public static final MaterialListJS INSTANCE = new MaterialListJS();

	public final Map<String, MaterialJS> map;
	public final MaterialJS air;
	public final MaterialJS wood;

	private MaterialListJS() {
		map = new HashMap<>();
		air = add("air", Material.AIR, SoundType.STONE);
		wood = add("wood", Material.WOOD, SoundType.WOOD);
		add("stone", Material.STONE, SoundType.STONE);
		add("metal", Material.METAL, SoundType.METAL);
		add("grass", Material.GRASS, SoundType.GRASS);
		add("crop", Material.PLANT, SoundType.CROP);
		add("dirt", Material.DIRT, SoundType.GRAVEL);
		add("water", Material.WATER, SoundType.STONE);
		add("lava", Material.LAVA, SoundType.STONE);
		add("leaves", Material.LEAVES, SoundType.GRASS);
		add("plant", Material.PLANT, SoundType.GRASS);
		add("sponge", Material.SPONGE, SoundType.GRASS);
		add("wool", Material.WOOL, SoundType.WOOL);
		add("sand", Material.SAND, SoundType.SAND);
		add("glass", Material.GLASS, SoundType.GLASS);
		add("explosive", Material.EXPLOSIVE, SoundType.GRASS);
		add("ice", Material.ICE, SoundType.GLASS);
		add("snow", Material.TOP_SNOW, SoundType.SNOW);
		add("clay", Material.CLAY, SoundType.GRAVEL);
		add("vegetable", Material.VEGETABLE, SoundType.GRASS);
		add("dragon_egg", Material.EGG, SoundType.STONE);
		add("portal", Material.PORTAL, SoundType.STONE);
		add("cake", Material.CAKE, SoundType.WOOL);
		add("web", Material.WEB, SoundType.WOOL);
		add("slime", Material.CLAY, SoundType.SLIME_BLOCK);
		add("honey", Material.CLAY, SoundType.HONEY_BLOCK);
		add("berry_bush", Material.PLANT, SoundType.SWEET_BERRY_BUSH);
		add("lantern", Material.METAL, SoundType.LANTERN);
		add("powder_snow", Material.POWDER_SNOW, SoundType.POWDER_SNOW);
		add("anvil", Material.HEAVY_METAL, SoundType.ANVIL);
		add("kelp", Material.WATER_PLANT, SoundType.WET_GRASS);
		add("sea_grass", Material.REPLACEABLE_WATER_PLANT, SoundType.WET_GRASS);
		add("coral", Material.STONE, SoundType.CORAL_BLOCK);
		add("bamboo", Material.BAMBOO, SoundType.BAMBOO);
		add("bamboo_sapling", Material.BAMBOO_SAPLING, SoundType.BAMBOO_SAPLING);
		add("scaffolding", Material.DECORATION, SoundType.SCAFFOLDING);
		add("crop", Material.PLANT, SoundType.CROP);
		add("hard_crop", Material.PLANT, SoundType.HARD_CROP);
		add("vine", Material.REPLACEABLE_PLANT, SoundType.VINE);
		add("nether_wart", Material.PLANT, SoundType.NETHER_WART);
		add("nylium", Material.STONE, SoundType.NYLIUM);
		add("roots", Material.REPLACEABLE_FIREPROOF_PLANT, SoundType.ROOTS);
		add("shroomlight", Material.GRASS, SoundType.SHROOMLIGHT);
		add("weeping_vines", Material.PLANT, SoundType.WEEPING_VINES);
		add("twisting_vines", Material.PLANT, SoundType.TWISTING_VINES);
		add("soul_sand", Material.SAND, SoundType.SOUL_SAND);
		add("soul_soil", Material.DIRT, SoundType.SOUL_SOIL);
		add("basalt", Material.STONE, SoundType.BASALT);
		add("wart_block", Material.GRASS, SoundType.WART_BLOCK);
		add("netherrack", Material.STONE, SoundType.NETHERRACK);
		add("nether_bricks", Material.STONE, SoundType.NETHER_BRICKS);
		add("nether_sprouts", Material.REPLACEABLE_FIREPROOF_PLANT, SoundType.NETHER_SPROUTS);
		add("nether_ore", Material.STONE, SoundType.NETHER_ORE);
		add("nether_gold_ore", Material.STONE, SoundType.NETHER_GOLD_ORE);
		add("bone", Material.STONE, SoundType.BONE_BLOCK);
		add("netherite", Material.METAL, SoundType.NETHERITE_BLOCK);
		add("ancient_debris", Material.METAL, SoundType.ANCIENT_DEBRIS);
		add("lodestone", Material.HEAVY_METAL, SoundType.LODESTONE);
		add("chain", Material.METAL, SoundType.CHAIN);
		add("gilded_blackstone", Material.STONE, SoundType.GILDED_BLACKSTONE);
		add("candle", Material.DECORATION, SoundType.CANDLE);
		add("amethyst", Material.AMETHYST, SoundType.AMETHYST);
		add("amethyst_cluster", Material.AMETHYST, SoundType.AMETHYST_CLUSTER);
		add("small_amethyst_bud", Material.AMETHYST, SoundType.SMALL_AMETHYST_BUD);
		add("medium_amethyst_bud", Material.AMETHYST, SoundType.MEDIUM_AMETHYST_BUD);
		add("large_amethyst_bud", Material.AMETHYST, SoundType.LARGE_AMETHYST_BUD);
		add("tuff", Material.STONE, SoundType.TUFF);
		add("calcite", Material.STONE, SoundType.CALCITE);
		add("dripstone", Material.STONE, SoundType.DRIPSTONE_BLOCK);
		add("pointed_dripstone", Material.STONE, SoundType.POINTED_DRIPSTONE);
		add("copper", Material.METAL, SoundType.COPPER);
		add("cave_vines", Material.PLANT, SoundType.CAVE_VINES);
		add("spore_blossom", Material.PLANT, SoundType.SPORE_BLOSSOM);
		add("azalea", Material.PLANT, SoundType.AZALEA);
		add("flowering_azalea", Material.PLANT, SoundType.FLOWERING_AZALEA);
		add("moss_carpet", Material.PLANT, SoundType.MOSS_CARPET);
		add("moss", Material.MOSS, SoundType.MOSS);
		add("big_dripleaf", Material.PLANT, SoundType.BIG_DRIPLEAF);
		add("small_dripleaf", Material.PLANT, SoundType.SMALL_DRIPLEAF);
		add("rooted_dirt", Material.DIRT, SoundType.ROOTED_DIRT);
		add("hanging_roots", Material.REPLACEABLE_PLANT, SoundType.HANGING_ROOTS);
		add("azalea_leaves", Material.LEAVES, SoundType.AZALEA_LEAVES);
		add("sculk_sensor", Material.SCULK, SoundType.SCULK_SENSOR);
		add("glow_lichen", Material.REPLACEABLE_PLANT, SoundType.GLOW_LICHEN);
		add("deepslate", Material.STONE, SoundType.DEEPSLATE);
		add("deepslate_bricks", Material.STONE, SoundType.DEEPSLATE_BRICKS);
		add("deepslate_tiles", Material.STONE, SoundType.DEEPSLATE_TILES);
		add("polished_deepslate", Material.STONE, SoundType.POLISHED_DEEPSLATE);
	}

	public MaterialJS of(Object o) {
		return o instanceof MaterialJS mat ? mat : map.getOrDefault(String.valueOf(o).toLowerCase(), wood);
	}

	public MaterialJS add(MaterialJS m) {
		map.put(m.getId(), m);
		return m;
	}

	public MaterialJS add(String s, Material m, SoundType e) {
		return add(new MaterialJS(s, m, e));
	}

	public MaterialJS get(String id) {
		var m = map.get(id);
		return m == null ? air : m;
	}

	public MaterialJS get(Material minecraftMaterial) {
		for (var materialJS : map.values()) {
			if (materialJS.getMinecraftMaterial() == minecraftMaterial) {
				return materialJS;
			}
		}

		return air;
	}
}