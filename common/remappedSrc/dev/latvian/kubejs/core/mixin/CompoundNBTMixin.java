package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.CompoundNBTKJS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

/**
 * @author LatvianModder
 */
@Mixin(CompoundTag.class)
public abstract class CompoundNBTMixin implements CompoundNBTKJS
{
	@Override
	@Accessor("tagMap")
	public abstract Map<String, Tag> getTagsKJS();

	@Override
	@Accessor("tagMap")
	public abstract void setTagsKJS(Map<String, Tag> map);
}