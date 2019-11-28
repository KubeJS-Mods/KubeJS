package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.recipe.type.CookingRecipeJS;
import dev.latvian.kubejs.recipe.type.CustomRecipeJS;
import dev.latvian.kubejs.recipe.type.ShapedRecipeJS;
import dev.latvian.kubejs.recipe.type.ShapelessRecipeJS;
import dev.latvian.kubejs.recipe.type.StonecuttingRecipeJS;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
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
		MinecraftForge.EVENT_BUS.addListener(this::registerRecipeHandlers);
	}

	private void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event)
	{
		event.getRegistry().register((DeletedRecipeSerializer.instance = new DeletedRecipeSerializer()).setRegistryName("deleted"));
	}

	private void registerRecipeHandlers(RegisterRecipeHandlersEvent event)
	{
		event.registerProvider("custom", CustomRecipeJS.PROVIDER);
		event.registerProvider("shaped", ShapedRecipeJS.PROVIDER);
		event.registerProvider("shapeless", ShapelessRecipeJS.PROVIDER);
		event.registerProvider("stonecutting", StonecuttingRecipeJS.PROVIDER);

		for (CookingRecipeJS.Type type : CookingRecipeJS.Type.values())
		{
			event.registerProvider(type.name, type.provider);
		}

		event.registerDeserializer(IRecipeSerializer.CRAFTING_SHAPED, ShapedRecipeJS.DESERIALIZER);
		event.registerDeserializer(IRecipeSerializer.CRAFTING_SHAPELESS, ShapelessRecipeJS.DESERIALIZER);
		event.registerDeserializer(IRecipeSerializer.STONECUTTING, StonecuttingRecipeJS.DESERIALIZER);

		for (CookingRecipeJS.Type type : CookingRecipeJS.Type.values())
		{
			event.registerDeserializer(type.serializer, type.deserializer);
		}
	}
}