package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.KubeJSObjects;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author LatvianModder
 */
public class KubeJSFluidEventHandler
{
	public void init()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Fluid.class, this::registry);
	}

	private void registry(RegistryEvent.Register<Fluid> event)
	{
		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values())
		{
			builder.stillFluid = new ForgeFlowingFluid.Source(builder.createProperties());
			builder.stillFluid.setRegistryName(builder.id);
			event.getRegistry().register(builder.stillFluid);

			builder.flowingFluid = new ForgeFlowingFluid.Flowing(builder.createProperties());
			builder.flowingFluid.setRegistryName(builder.id.getNamespace() + ":flowing_" + builder.id.getPath());
			event.getRegistry().register(builder.flowingFluid);
		}
	}
}