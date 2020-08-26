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
	@Inject(method = "func_242226_a", at = @At("HEAD"))
	private void customTags(Map<ResourceLocation, ITag.Builder> map, CallbackInfoReturnable<ITagCollection<T>> ci)
	{
		customTagsKJS(map);
	}

	@Override
	@Accessor("field_242220_d")
	public abstract Function<ResourceLocation, Optional<T>> getRegistryKJS();

	@Override
	@Accessor("field_242221_e")
	public abstract String getResourceLocationPrefixKJS();

	@Override
	@Accessor("field_242222_f")
	public abstract String getItemTypeNameKJS();
}