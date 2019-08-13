package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.item.ItemJS;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID, value = Side.CLIENT)
public class KubeJSClientEventHandler
{
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		for (Map.Entry<Item, String> entry : ItemJS.ITEM_MODELS.entrySet())
		{
			ModelLoader.setCustomModelResourceLocation(entry.getKey(), 0, new ModelResourceLocation(entry.getValue()));
		}
	}
}