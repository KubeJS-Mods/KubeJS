package dev.latvian.mods.kubejs.block;

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

		// Legacy
		add("rock", Material.STONE, SoundType.STONE);
		add("iron", Material.METAL, SoundType.METAL);
		add("organic", Material.GRASS, SoundType.GRASS);
		add("earth", Material.DIRT, SoundType.GRAVEL);
		add("plants", Material.PLANT, SoundType.GRASS);
		add("tnt", Material.EXPLOSIVE, SoundType.GRASS);
		add("gourd", Material.VEGETABLE, SoundType.GRASS);
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