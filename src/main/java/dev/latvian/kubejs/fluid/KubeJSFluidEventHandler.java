package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.KubeJSObjects;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.event.RegistryEvent;
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
			builder.fluid = new FluidJS(builder, () -> builder.fluid, () -> builder.bucketItem);
			builder.fluid.setRegistryName(builder.id);
			event.getRegistry().register(builder.fluid);
		}
	}
}