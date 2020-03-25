package dev.latvian.kubejs.core;

import net.minecraftforge.common.ToolType;

/**
 * @author LatvianModder
 */
public interface BlockKJS
{
	void setHardnessKJS(float hardness);

	void setResistanceKJS(float resistance);

	void setLightLevelKJS(int lightLevel);

	void setHarvestToolKJS(ToolType type);

	void setHarvestLevelKJS(int level);
}