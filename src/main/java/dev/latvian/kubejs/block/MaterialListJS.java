package dev.latvian.kubejs.block;

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
		air = add("air", Material.AIR);
		add("wood", Material.WOOD);
		add("rock", Material.ROCK);
		add("iron", Material.IRON);
		add("organic", Material.GRASS);
		add("earth", Material.GROUND);
		add("water", Material.WATER);
		add("lava", Material.LAVA);
		add("leaves", Material.LEAVES);
		add("plants", Material.PLANTS);
		add("sponge", Material.SPONGE);
		add("wool", Material.CLOTH);
		add("sand", Material.SAND);
		add("glass", Material.GLASS);
		add("tnt", Material.TNT);
		add("coral", Material.CORAL);
		add("ice", Material.ICE);
		add("snow", Material.SNOW);
		add("clay", Material.CLAY);
		add("gourd", Material.GOURD);
		add("dragon_egg", Material.DRAGON_EGG);
		add("portal", Material.PORTAL);
		add("cake", Material.CAKE);
		add("web", Material.WEB);
	}

	public MaterialJS add(MaterialJS m)
	{
		map.put(m.id, m);
		return m;
	}

	public MaterialJS add(String s, Material m)
	{
		return add(new MaterialJS(s, m));
	}

	public MaterialJS get(String id)
	{
		MaterialJS m = map.get(id);
		return m == null ? air : m;
	}
}