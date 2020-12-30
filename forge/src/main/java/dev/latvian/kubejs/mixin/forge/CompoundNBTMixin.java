package dev.latvian.kubejs.mixin.forge;

import dev.latvian.kubejs.core.CompoundNBTKJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * @author LatvianModder
 */
@Mixin(CompoundTag.class)
public abstract class CompoundNBTMixin implements CompoundNBTKJS
{
	@Override
	@Accessor("tags")
	public abstract Map<String, Tag> getTagsKJS();

	@Override
	@Accessor("tags")
	public abstract void setTagsKJS(Map<String, Tag> map);
}
