package dev.latvian.kubejs.recipe;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author LatvianModder
 */
public class KubeJSRecipeEventHandler
{
	public void init()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IRecipeSerializer.class, this::registerRecipeSerializers);
	}

	private void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event)
	{
		event.getRegistry().register((DeletedRecipeSerializer.instance = new DeletedRecipeSerializer()).setRegistryName("deleted"));
	}
}