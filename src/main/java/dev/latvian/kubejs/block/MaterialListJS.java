package dev.latvian.kubejs.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class MaterialListJS
{
	public static final MaterialListJS INSTANCE = new MaterialListJS();

	public final Map<String, MaterialJS> map;
	public final MaterialJS air;

	private MaterialListJS()
	{
		map = new HashMap<>();
		air = add("air", Material.AIR, SoundType.STONE);
		add("wood", Material.WOOD, SoundType.WOOD);
		add("rock", Material.ROCK, SoundType.STONE);
		add("iron", Material.IRON, SoundType.METAL);
		add("organic", Material.ORGANIC, SoundType.PLANT);
		add("earth", Material.EARTH, SoundType.GROUND);
		add("water", Material.WATER, SoundType.STONE);
		add("lava", Material.LAVA, SoundType.STONE);
		add("leaves", Material.LEAVES, SoundType.PLANT);
		add("plants", Material.PLANTS, SoundType.PLANT);
		add("sponge", Material.SPONGE, SoundType.PLANT);
		add("wool", Material.WOOL, SoundType.CLOTH);
		add("sand", Material.SAND, SoundType.SAND);
		add("glass", Material.GLASS, SoundType.GLASS);
		add("tnt", Material.TNT, SoundType.PLANT);
		add("coral", Material.CORAL, SoundType.CORAL);
		add("ice", Material.ICE, SoundType.GLASS);
		add("snow", Material.SNOW, SoundType.SNOW);
		add("clay", Material.CLAY, SoundType.GROUND);
		add("gourd", Material.GOURD, SoundType.PLANT);
		add("dragon_egg", Material.DRAGON_EGG, SoundType.STONE);
		add("portal", Material.PORTAL, SoundType.STONE);
		add("cake", Material.CAKE, SoundType.CLOTH);
		add("web", Material.WEB, SoundType.CLOTH);
		add("slime", Material.CLAY, SoundType.SLIME);
		add("honey", Material.CLAY, SoundType.HONEY);
		add("berry_bush", Material.PLANTS, SoundType.SWEET_BERRY_BUSH);
		add("lantern", Material.IRON, SoundType.LANTERN);
	}

	public MaterialJS add(MaterialJS m)
	{
		map.put(m.getId(), m);
		return m;
	}

	public MaterialJS add(String s, Material m, SoundType e)
	{
		return add(new MaterialJS(s, m, e));
	}

	public MaterialJS get(String id)
	{
		MaterialJS m = map.get(id);
		return m == null ? air : m;
	}

	public MaterialJS get(Material minecraftMaterial)
	{
		for (MaterialJS materialJS : map.values())
		{
			if (materialJS.getMinecraftMaterial() == minecraftMaterial)
			{
				return materialJS;
			}
		}

		return air;
	}
}