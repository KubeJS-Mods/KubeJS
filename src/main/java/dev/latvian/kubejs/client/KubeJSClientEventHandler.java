package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.item.BlockItemJS;
import dev.latvian.kubejs.item.ItemJS;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID, value = Side.CLIENT)
public class KubeJSClientEventHandler
{
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		for (Item item : Item.REGISTRY)
		{
			if (item instanceof ItemJS)
			{
				ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(((ItemJS) item).properties.model));
			}
			else if (item instanceof BlockItemJS)
			{
				ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(((BlockItemJS) item).properties.model));
			}
		}
	}
}