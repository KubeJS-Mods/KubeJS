package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.BlockKJS;
import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author LatvianModder
 */
@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin implements BlockKJS
{
	/* FIXME
	@Override
	@Accessor("blockHardness")
	public abstract void setHardnessKJS(float f);

	@Override
	@Accessor(value = "field_235689_au_", remap = false)
	public abstract void setResistanceKJS(float f);

	@Override
	@Accessor("lightValue")
	public abstract void setLightLevelKJS(int f);

	@Override
	@Accessor(value = "harvestTool", remap = false)
	public abstract void setHarvestToolKJS(ToolType v);

	@Override
	@Accessor(value = "harvestLevel", remap = false)
	public abstract void setHarvestLevelKJS(int v);
	 */
}