package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author LatvianModder
 */
@RemapPrefixForJS("kjs$")
@Mixin(value = ResourceKey.class, priority = 1001)
public abstract class ResourceKeyMixin implements SpecialEquality {
	@Shadow
	@Final
	private ResourceLocation location;

	@Unique
	public String kjs$getNamespace() {
		return location.getNamespace();
	}

	@Unique
	public String kjs$getPath() {
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
			return location.toString().equals(String.valueOf(o));
		}
	}
}
