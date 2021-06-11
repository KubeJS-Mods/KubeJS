package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author LatvianModder
 */
@Mixin(ResourceKey.class)
public abstract class ResourceKeyMixin implements SpecialEquality {
	@Shadow
	@Final
	private ResourceLocation location;

	@RemapForJS("getNamespace")
	public String kjs_getNamespace() {
		return location.getNamespace();
	}

	@RemapForJS("getPath")
	public String kjs_getPath() {
		return location.getPath();
	}

	@Override
	public boolean specialEquals(Object o, boolean shallow) {
		if (this == o) {
			return true;
		} else if (o instanceof ResourceKey) {
			return false;
		} else if (o instanceof ResourceLocation) {
			return location.equals(o);
		} else {
			String s = String.valueOf(o);
			return location.getNamespace().equals(UtilsJS.getNamespace(s)) && location.getPath().equals(UtilsJS.getPath(s));
		}
	}
}
