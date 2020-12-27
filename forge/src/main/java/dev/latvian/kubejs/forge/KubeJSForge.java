package dev.latvian.kubejs.forge;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.block.forge.MissingMappingEventJS;
import dev.latvian.kubejs.entity.ItemEntityJS;
import dev.latvian.kubejs.entity.forge.CheckLivingEntitySpawnEventJS;
import dev.latvian.kubejs.entity.forge.LivingEntityDropsEventJS;
import dev.latvian.kubejs.integration.IntegrationManager;
import dev.latvian.kubejs.item.forge.ItemDestroyedEventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.world.gen.WorldgenAddEventJS;
import dev.latvian.kubejs.world.gen.WorldgenRemoveEventJS;
import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraft.block.Block;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

@Mod(KubeJS.MOD_ID)
public class KubeJSForge
{
	public KubeJSForge() throws Throwable
	{
		EventBuses.registerModEventBus(KubeJS.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
		KubeJS kubeJS = new KubeJS();
		kubeJS.setup();
		IntegrationManager.init();
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		MinecraftForge.EVENT_BUS.addGenericListener(Block.class, KubeJSForge::missingBlockMappings);
		MinecraftForge.EVENT_BUS.addGenericListener(Item.class, KubeJSForge::missingItemMappings);
		MinecraftForge.EVENT_BUS.addListener(KubeJSForge::itemDestroyed);

		MinecraftForge.EVENT_BUS.addListener(KubeJSForge::livingDrops);
		MinecraftForge.EVENT_BUS.addListener(KubeJSForge::checkLivingSpawn);

		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, KubeJSForge::onBiomesLoad);

		kubeJS.loadComplete();
	}

	private static void missingBlockMappings(RegistryEvent.MissingMappings<Block> event)
	{
		new MissingMappingEventJS<>(event, ForgeRegistries.BLOCKS::getValue).post(ScriptType.STARTUP, KubeJSEvents.BLOCK_MISSING_MAPPINGS);
	}

	private static void missingItemMappings(RegistryEvent.MissingMappings<Item> event)
	{
		new MissingMappingEventJS<>(event, ForgeRegistries.ITEMS::getValue).post(ScriptType.STARTUP, KubeJSEvents.ITEM_MISSING_MAPPINGS);
	}

	private static void itemDestroyed(PlayerDestroyItemEvent event)
	{
		if (event.getPlayer() instanceof ServerPlayerEntity)
		{
			new ItemDestroyedEventJS(event).post(KubeJSEvents.ITEM_DESTROYED);
		}
	}

	private static void livingDrops(LivingDropsEvent event)
	{
		if (event.getEntity().level.isClientSide())
		{
			return;
		}

		LivingEntityDropsEventJS e = new LivingEntityDropsEventJS(event);

		if (e.post(KubeJSEvents.ENTITY_DROPS))
		{
			event.setCanceled(true);
		}
		else if (e.eventDrops != null)
		{
			event.getDrops().clear();

			for (ItemEntityJS ie : e.eventDrops)
			{
				event.getDrops().add((ItemEntity) ie.minecraftEntity);
			}
		}
	}

	private static void checkLivingSpawn(LivingSpawnEvent.CheckSpawn event)
	{
		if (ServerJS.instance != null && ServerJS.instance.overworld != null && !event.getWorld().isClientSide() && new CheckLivingEntitySpawnEventJS(event).post(ScriptType.SERVER, KubeJSEvents.ENTITY_CHECK_SPAWN))
		{
			event.setResult(Event.Result.DENY);
		}
	}

	private static void onBiomesLoad(BiomeLoadingEvent event)
	{
		new WorldgenRemoveEventJS(event.getGeneration()).post(ScriptType.SERVER, KubeJSEvents.SERVER_WORLDGEN_REMOVE);
		new WorldgenAddEventJS(event.getGeneration()).post(ScriptType.SERVER, KubeJSEvents.SERVER_WORLDGEN_ADD);
	}
}
