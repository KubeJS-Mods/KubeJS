package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.BlockKJS;
import net.minecraft.block.Block;
import net.minecraftforge.common.ToolType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(Block.class)
public abstract class BlockMixin implements BlockKJS
{
	@Override
	@Accessor("blockHardness")
	public abstract void setHardnessKJS(float f);

	@Override
	@Accessor("blockResistance")
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
}