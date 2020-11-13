package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.script.ScriptsLoadedEvent;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;

/**
 * @author LatvianModder
 */
public class KubeJSFluidEventHandler
{
	public static void init()
	{
		ScriptsLoadedEvent.EVENT.register(KubeJSFluidEventHandler::registry);
	}

	private static FlowingFluid buildFluid(boolean source, FluidBuilder builder)
	{
		throw new AssertionError();
	}

	private static void registry()
	{
		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values())
		{
			Registries.get(KubeJS.MOD_ID).get(Registry.FLUID_REGISTRY).register(builder.id, () ->
					builder.stillFluid = buildFluid(true, builder));
			Registries.get(KubeJS.MOD_ID).get(Registry.FLUID_REGISTRY).register(new ResourceLocation(builder.id.getNamespace(), "flowing_" + builder.id.getPath()), () ->
					builder.flowingFluid = buildFluid(false, builder));
		}
	}
}