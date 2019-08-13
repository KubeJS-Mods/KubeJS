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
	public final MaterialJS grass;
	public final MaterialJS ground;
	public final MaterialJS wood;
	public final MaterialJS rock;
	public final MaterialJS iron;
	public final MaterialJS anvil;
	public final MaterialJS water;
	public final MaterialJS lava;
	public final MaterialJS leaves;
	public final MaterialJS plants;
	public final MaterialJS vine;
	public final MaterialJS sponge;
	public final MaterialJS cloth;
	public final MaterialJS fire;
	public final MaterialJS sand;
	public final MaterialJS circuits;
	public final MaterialJS carpet;
	public final MaterialJS glass;
	public final MaterialJS redstone_light;
	public final MaterialJS tnt;
	public final MaterialJS coral;
	public final MaterialJS ice;
	public final MaterialJS packed_ice;
	public final MaterialJS snow;
	public final MaterialJS crafted_snow;
	public final MaterialJS cactus;
	public final MaterialJS clay;
	public final MaterialJS gourd;
	public final MaterialJS dragon_egg;
	public final MaterialJS portal;
	public final MaterialJS cake;
	public final MaterialJS web;
	public final MaterialJS piston;
	public final MaterialJS barrier;
	public final MaterialJS structure_void;

	private MaterialListJS()
	{
		map = new HashMap<>();
		air = add("air", Material.AIR);
		grass = add("grass", Material.GRASS);
		ground = add("ground", Material.GROUND);
		wood = add("wood", Material.WOOD);
		rock = add("rock", Material.ROCK);
		iron = add("iron", Material.IRON);
		anvil = add("anvil", Material.ANVIL);
		water = add("water", Material.WATER);
		lava = add("lava", Material.LAVA);
		leaves = add("leaves", Material.LEAVES);
		plants = add("plants", Material.PLANTS);
		vine = add("vine", Material.VINE);
		sponge = add("sponge", Material.SPONGE);
		cloth = add("cloth", Material.CLOTH);
		fire = add("fire", Material.FIRE);
		sand = add("sand", Material.SAND);
		circuits = add("circuits", Material.CIRCUITS);
		carpet = add("carpet", Material.CARPET);
		glass = add("glass", Material.GLASS);
		redstone_light = add("redstone_light", Material.REDSTONE_LIGHT);
		tnt = add("tnt", Material.TNT);
		coral = add("coral", Material.CORAL);
		ice = add("ice", Material.ICE);
		packed_ice = add("packed_ice", Material.PACKED_ICE);
		snow = add("snow", Material.SNOW);
		crafted_snow = add("crafted_snow", Material.CRAFTED_SNOW);
		cactus = add("cactus", Material.CACTUS);
		clay = add("clay", Material.CLAY);
		gourd = add("gourd", Material.GOURD);
		dragon_egg = add("dragon_egg", Material.DRAGON_EGG);
		portal = add("portal", Material.PORTAL);
		cake = add("cake", Material.CAKE);
		web = add("web", Material.WEB);
		piston = add("piston", Material.PISTON);
		barrier = add("barrier", Material.BARRIER);
		structure_void = add("structure_void", Material.STRUCTURE_VOID);
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