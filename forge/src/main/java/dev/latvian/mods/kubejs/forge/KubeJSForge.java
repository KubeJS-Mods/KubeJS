package dev.latvian.mods.kubejs.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.block.forge.MissingMappingEventJS;
import dev.latvian.mods.kubejs.entity.ItemEntityJS;
import dev.latvian.mods.kubejs.entity.forge.CheckLivingEntitySpawnEventJS;
import dev.latvian.mods.kubejs.entity.forge.LivingEntityDropsEventJS;
import dev.latvian.mods.kubejs.integration.IntegrationManager;
import dev.latvian.mods.kubejs.item.forge.ItemDestroyedEventJS;
import dev.latvian.mods.kubejs.item.ingredient.forge.CustomPredicateIngredient;
import dev.latvian.mods.kubejs.item.ingredient.forge.IgnoreNBTIngredient;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ServerJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(KubeJS.MOD_ID)
public class KubeJSForge {
	public KubeJSForge() throws Throwable {
		EventBuses.registerModEventBus(KubeJS.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
		FMLJavaModLoadingContext.get().getModEventBus().addListener(KubeJSForge::loadComplete);
		KubeJS.instance = new KubeJS();
		KubeJS.instance.setup();
		IntegrationManager.init();
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

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

		CraftingHelper.register(new ResourceLocation("kubejs:custom_predicate"), CustomPredicateIngredient.SERIALIZER);
		CraftingHelper.register(new ResourceLocation("kubejs:ignore_nbt"), IgnoreNBTIngredient.SERIALIZER);
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
