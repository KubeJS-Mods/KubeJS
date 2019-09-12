package dev.latvian.kubejs.block;

import net.minecraft.block.material.Material;

/**
 * @author LatvianModder
 */
public class MaterialJS
{
	private final String id;
	private final transient Material material;

	public MaterialJS(String i, Material m)
	{
		id = i;
		material = m;
	}

	public String getId()
	{
		return id;
	}

	public Material getMaterial()
	{
		return material;
	}
}