package dev.latvian.kubejs.mixin.forge;

import dev.latvian.kubejs.core.CompoundNBTKJS;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * @author LatvianModder
 */
@Mixin(CompoundNBT.class)
public abstract class CompoundNBTMixin implements CompoundNBTKJS
{
	@Override
	@Accessor("tags")
	public abstract Map<String, INBT> getTagsKJS();

	@Override
	@Accessor("tags")
	public abstract void setTagsKJS(Map<String, INBT> map);
}
