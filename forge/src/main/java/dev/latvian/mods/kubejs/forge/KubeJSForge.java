package dev.latvian.mods.kubejs.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.entity.forge.LivingEntityDropsEventJS;
import dev.latvian.mods.kubejs.item.forge.ItemDestroyedEventJS;
import dev.latvian.mods.kubejs.platform.forge.IngredientPlatformHelperImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.registries.RegisterEvent;

@Mod(KubeJS.MOD_ID)
public class KubeJSForge {
	public KubeJSForge() throws Throwable {
		EventBuses.registerModEventBus(KubeJS.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
		FMLJavaModLoadingContext.get().getModEventBus().addListener(KubeJSForge::loadComplete);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(KubeJSForge::initRegistries);
		KubeJS.instance = new KubeJS();
		KubeJS.instance.setup();
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		MinecraftForge.EVENT_BUS.addListener(KubeJSForge::itemDestroyed);
		MinecraftForge.EVENT_BUS.addListener(KubeJSForge::livingDrops);

		if (!CommonProperties.get().serverOnly) {
			ForgeMod.enableMilkFluid();
			IngredientPlatformHelperImpl.register();
		}

	}

	private static void initRegistries(RegisterEvent event) {
		KubeJSRegistries.init(event.getRegistryKey());
	}

	private static void loadComplete(FMLLoadCompleteEvent event) {
		KubeJS.instance.loadComplete();
	}

	private static void itemDestroyed(PlayerDestroyItemEvent event) {
		if (event.getEntity() instanceof ServerPlayer) {
			ForgeKubeJSEvents.ITEM_DESTROYED.post(ItemWrapper.getId(event.getOriginal().getItem()), new ItemDestroyedEventJS(event));
		}
	}

	private static void livingDrops(LivingDropsEvent event) {
		if (event.getEntity().level.isClientSide()) {
			return;
		}

		var e = new LivingEntityDropsEventJS(event);

		if (ForgeKubeJSEvents.ENTITY_DROPS.post(e.getEntity().getType(), e)) {
			event.setCanceled(true);
		} else if (e.eventDrops != null) {
			event.getDrops().clear();
			event.getDrops().addAll(e.eventDrops);
		}
	}
}
