package dev.latvian.kubejs.mixin.common;

import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(ResourceLocation.class)
public abstract class ResourceLocationMixin implements SpecialEquality {
	@Accessor("namespace")
	@RemapForJS("getNamespace")
	public abstract String kjs_getNamespace();

	@Accessor("path")
	@RemapForJS("getPath")
	public abstract String kjs_getPath();

	@Override
	public boolean specialEquals(Object o, boolean shallow) {
		return equals(o instanceof ResourceLocation ? o : new ResourceLocation(String.valueOf(o)));
	}
}
