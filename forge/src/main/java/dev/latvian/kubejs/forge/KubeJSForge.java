package dev.latvian.kubejs.forge;

import dev.latvian.kubejs.CommonProperties;
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
import dev.architectury.architectury.platform.forge.EventBuses;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

@Mod(KubeJS.MOD_ID)
public class KubeJSForge {
	public KubeJSForge() throws Throwable {
		EventBuses.registerModEventBus(KubeJS.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
		FMLJavaModLoadingContext.get().getModEventBus().addListener(KubeJSForge::loadComplete);
		KubeJS.instance = new KubeJS();
		KubeJS.instance.setup();
		IntegrationManager.init();
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		MinecraftForge.EVENT_BUS.addGenericListener(Block.class, KubeJSForge::missingBlockMappings);
		MinecraftForge.EVENT_BUS.addGenericListener(Item.class, KubeJSForge::missingItemMappings);
		MinecraftForge.EVENT_BUS.addListener(KubeJSForge::itemDestroyed);

		MinecraftForge.EVENT_BUS.addListener(KubeJSForge::livingDrops);
		MinecraftForge.EVENT_BUS.addListener(KubeJSForge::checkLivingSpawn);

		if (!CommonProperties.get().serverOnly) {
			// Yes this is stupid but for now I will do this until more mods update to 1.16.5 properly, because we never know how many mods hardcode [.4]. Use ForgeMod.enableMilkFluid(); after a while

			try {
				ForgeMod.class.getDeclaredMethod("enableMilkFluid").invoke(null);
			} catch (Throwable ex) {
			}
		}
	}

	private static void loadComplete(FMLLoadCompleteEvent event) {
		KubeJS.instance.loadComplete();
	}

	private static void missingBlockMappings(RegistryEvent.MissingMappings<Block> event) {
		new MissingMappingEventJS<>(event, ForgeRegistries.BLOCKS::getValue).post(ScriptType.STARTUP, KubeJSEvents.BLOCK_MISSING_MAPPINGS);
	}

	private static void missingItemMappings(RegistryEvent.MissingMappings<Item> event) {
		new MissingMappingEventJS<>(event, ForgeRegistries.ITEMS::getValue).post(ScriptType.STARTUP, KubeJSEvents.ITEM_MISSING_MAPPINGS);
	}

	private static void itemDestroyed(PlayerDestroyItemEvent event) {
		if (event.getPlayer() instanceof ServerPlayer) {
			new ItemDestroyedEventJS(event).post(KubeJSEvents.ITEM_DESTROYED);
		}
	}

	private static void livingDrops(LivingDropsEvent event) {
		if (event.getEntity().level.isClientSide()) {
			return;
		}

		LivingEntityDropsEventJS e = new LivingEntityDropsEventJS(event);

		if (e.post(KubeJSEvents.ENTITY_DROPS)) {
			event.setCanceled(true);
		} else if (e.eventDrops != null) {
			event.getDrops().clear();

			for (ItemEntityJS ie : e.eventDrops) {
				event.getDrops().add((ItemEntity) ie.minecraftEntity);
			}
		}
	}

	private static void checkLivingSpawn(LivingSpawnEvent.CheckSpawn event) {
		if (ServerJS.instance != null && ServerJS.instance.overworld != null && !event.getWorld().isClientSide() && new CheckLivingEntitySpawnEventJS(event).post(ScriptType.SERVER, KubeJSEvents.ENTITY_CHECK_SPAWN)) {
			event.setResult(Event.Result.DENY);
		}
	}
}
