package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.TagCollectionKJS;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * @author LatvianModder
 */
@Mixin(TagCollection.class)
public abstract class TagCollectionMixin<T> implements TagCollectionKJS
{
	@Inject(method = "registerAll", at = @At("HEAD"))
	private void customTags(Map<ResourceLocation, ITag.Builder> map, CallbackInfo ci)
	{
		customTagsKJS(map);
	}

	@Override
	@Accessor("resourceLocationPrefix")
	public abstract String getResourceLocationPrefixKJS();

	@Override
	@Accessor("itemTypeName")
	public abstract String getItemTypeNameKJS();
}