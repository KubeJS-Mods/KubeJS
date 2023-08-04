package dev.latvian.mods.kubejs.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.WorldgenEvents;
import dev.latvian.mods.kubejs.entity.forge.LivingEntityDropsEventJS;
import dev.latvian.mods.kubejs.item.forge.ItemDestroyedEventJS;
import dev.latvian.mods.kubejs.platform.forge.IngredientForgeHelper;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.registries.RegisterEvent;

@Mod(KubeJS.MOD_ID)
public class KubeJSForge {
	public KubeJSForge() throws Throwable {
		var bus = FMLJavaModLoadingContext.get().getModEventBus();
		EventBuses.registerModEventBus(KubeJS.MOD_ID, bus);
		bus.addListener(EventPriority.LOW, KubeJSForge::loadComplete);
		bus.addListener(EventPriority.LOW, KubeJSForge::initRegistries);
		bus.addListener(EventPriority.LOW, KubeJSForge::commonSetup);

		KubeJS.instance = new KubeJS();
		KubeJS.instance.setup();
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		MinecraftForge.EVENT_BUS.addListener(KubeJSForge::itemDestroyed);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, KubeJSForge::livingDrops);

		if (!CommonProperties.get().serverOnly) {
			ForgeMod.enableMilkFluid();
			IngredientForgeHelper.register();
		}

		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> KubeJSForgeClient::new);
	}

	private static void initRegistries(RegisterEvent event) {
		var info = RegistryInfo.of(event.getRegistryKey());
		info.registerObjects((id, supplier) -> event.register(UtilsJS.cast(info.key), id, supplier));
	}

	private static void commonSetup(FMLCommonSetupEvent event) {
		WorldgenEvents.post();
	}

	private static void loadComplete(FMLLoadCompleteEvent event) {
		KubeJS.instance.loadComplete();
	}

	private static void itemDestroyed(PlayerDestroyItemEvent event) {
		if (ForgeKubeJSEvents.ITEM_DESTROYED.hasListeners()) {
			ForgeKubeJSEvents.ITEM_DESTROYED.post(event.getEntity(), event.getOriginal().getItem(), new ItemDestroyedEventJS(event));
		}
	}

	private static void livingDrops(LivingDropsEvent event) {
		if (ForgeKubeJSEvents.ENTITY_DROPS.hasListeners()) {
			var e = new LivingEntityDropsEventJS(event);

			if (ForgeKubeJSEvents.ENTITY_DROPS.post(event.getEntity(), e.getEntity().getType(), e).interruptFalse()) {
				event.setCanceled(true);
			} else if (e.eventDrops != null) {
				event.getDrops().clear();
				event.getDrops().addAll(e.eventDrops);
			}
		}
	}
}
