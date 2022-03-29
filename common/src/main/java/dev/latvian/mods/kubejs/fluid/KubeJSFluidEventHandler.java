package dev.latvian.mods.kubejs.fluid;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.material.FlowingFluid;

/**
 * @author LatvianModder
 */
public class KubeJSFluidEventHandler {
	@ExpectPlatform
	public static FlowingFluid buildFluid(boolean source, FluidBuilder builder) {
		throw new AssertionError();
	}
}