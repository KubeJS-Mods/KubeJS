package dev.latvian.kubejs.block;

import dev.latvian.kubejs.MinecraftClass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/**
 * @author LatvianModder
 */
public class MaterialJS
{
	private final String id;
	private final Material minecraftMaterial;
	private final SoundType sound;

	public MaterialJS(String i, Material m, SoundType s)
	{
		id = i;
		minecraftMaterial = m;
		sound = s;
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

	@MinecraftClass
	public SoundType getSound()
	{
		return sound;
	}
}