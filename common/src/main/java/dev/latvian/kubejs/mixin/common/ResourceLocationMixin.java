package dev.latvian.kubejs.mixin.common;

import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(ResourceLocation.class)
public abstract class ResourceLocationMixin
{
	@Accessor
	@RemapForJS("getNamespace")
	public abstract String getNamespace();

	@Accessor
	@RemapForJS("getPath")
	public abstract String getPath();
}
