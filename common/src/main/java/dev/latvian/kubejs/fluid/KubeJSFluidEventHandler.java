package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;

/**
 * @author LatvianModder
 */
public class KubeJSFluidEventHandler {
	public static void init() {
		registry();
	}

	@ExpectPlatform
	private static FlowingFluid buildFluid(boolean source, FluidBuilder builder) {
		throw new AssertionError();
	}

	private static void registry() {
		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values()) {
			KubeJSRegistries.fluids().register(builder.id, () -> builder.stillFluid = buildFluid(true, builder));
			KubeJSRegistries.fluids().register(new ResourceLocation(builder.id.getNamespace(), "flowing_" + builder.id.getPath()), () -> builder.flowingFluid = buildFluid(false, builder));
		}
	}
}