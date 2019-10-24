package dev.latvian.kubejs.block;

import dev.latvian.kubejs.MinecraftClass;
import net.minecraft.block.material.Material;

/**
 * @author LatvianModder
 */
public class MaterialJS
{
	private final String id;
	private final Material minecraftMaterial;

	public MaterialJS(String i, Material m)
	{
		id = i;
		minecraftMaterial = m;
	}

	public String getId()
	{
		return id;
	}

	@MinecraftClass
	public Material getMinecraftMaterial()
	{
		return minecraftMaterial;
	}
}