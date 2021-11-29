package dev.latvian.mods.kubejs.core;

import net.minecraft.world.level.material.Material;

/**
 * @author LatvianModder
 */
public interface BlockStateKJS {
	void setMaterialKJS(Material v);

	void setDestroySpeedKJS(float v);

	void setRequiresToolKJS(boolean v);

	void setLightEmissionKJS(int v);
}
