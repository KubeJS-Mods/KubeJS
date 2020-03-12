package dev.latvian.kubejs.mixin;

import dev.latvian.kubejs.KubeJSCore;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
@Mixin(TagCollection.class)
public abstract class TagCollectionMixin<T>
{
	@Shadow
	private String itemTypeName;

	@Shadow
	private Function<ResourceLocation, Optional<T>> resourceLocationToItem;

	@Inject(method = "registerAll", at = @At("RETURN"))
	public void customTags(Map<ResourceLocation, Tag.Builder<T>> map, CallbackInfo ci)
	{
		KubeJSCore.customTags((TagCollection<T>) (Object) this, itemTypeName, resourceLocationToItem);
	}
}