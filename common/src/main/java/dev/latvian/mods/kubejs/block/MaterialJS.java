package dev.latvian.mods.kubejs.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

/**
 * @author LatvianModder
 */
public class MaterialJS {
	private final String id;
	private final Material minecraftMaterial;
	private final SoundType sound;

	public MaterialJS(String i, Material m, SoundType s) {
		id = i;
		minecraftMaterial = m;
		sound = s;
	}

	public String getId() {
		return id;
	}

	public Material getMinecraftMaterial() {
		return minecraftMaterial;
	}

	public SoundType getSound() {
		return sound;
	}
}