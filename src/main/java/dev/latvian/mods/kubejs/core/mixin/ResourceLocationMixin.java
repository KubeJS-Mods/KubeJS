package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ResourceLocation.class, priority = 1001)
public abstract class ResourceLocationMixin implements SpecialEquality {
	@Override
	public boolean specialEquals(Context cx, Object o, boolean shallow) {
		return equals(o instanceof ResourceLocation ? o : toString().equals(String.valueOf(o)));
	}
}
