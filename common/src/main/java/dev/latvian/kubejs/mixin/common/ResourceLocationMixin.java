package dev.latvian.kubejs.mixin.common;

import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author LatvianModder
 */
@Mixin(ResourceLocation.class)
public abstract class ResourceLocationMixin implements SpecialEquality {
	@Shadow
	@RemapForJS("getNamespace")
	public abstract String getNamespace();

	@Shadow
	@RemapForJS("getPath")
	public abstract String getPath();

	@Override
	public boolean specialEquals(Object o, boolean shallow) {
		return equals(o instanceof ResourceLocation ? o : new ResourceLocation(String.valueOf(o)));
	}
}
