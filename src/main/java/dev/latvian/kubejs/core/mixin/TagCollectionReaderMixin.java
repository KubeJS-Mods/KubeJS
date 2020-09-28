package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.TagCollectionKJS;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.TagCollectionReader;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
@Mixin(TagCollectionReader.class)
public abstract class TagCollectionReaderMixin<T> implements TagCollectionKJS<T>
{
	@Inject(method = "buildTagCollectionFromMap", at = @At("HEAD"))
	private void customTags(Map<ResourceLocation, ITag.Builder> map, CallbackInfoReturnable<ITagCollection<T>> ci)
	{
		customTagsKJS(map);
	}

	@Override
	@Accessor("idToTagFunction")
	public abstract Function<ResourceLocation, Optional<T>> getRegistryKJS();

	@Override
	@Accessor("path")
	public abstract String getResourceLocationPrefixKJS();

	@Override
	@Accessor("tagType")
	public abstract String getItemTypeNameKJS();
}