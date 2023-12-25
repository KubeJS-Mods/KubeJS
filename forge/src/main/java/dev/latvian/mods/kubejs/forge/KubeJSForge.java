package dev.latvian.mods.kubejs.forge;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.entity.forge.LivingEntityDropsEventJS;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabCallback;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabEvent;
import dev.latvian.mods.kubejs.item.creativetab.KubeJSCreativeTabs;
import dev.latvian.mods.kubejs.item.forge.ItemDestroyedEventJS;
import dev.latvian.mods.kubejs.platform.forge.IngredientForgeHelper;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerDestroyItemEvent;
import net.neoforged.neoforge.network.NetworkConstants;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

@Mod(KubeJS.MOD_ID)
public class KubeJSForge {
	public KubeJSForge() throws Throwable {
		var bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(EventPriority.LOW, KubeJSForge::loadComplete);
		bus.addListener(EventPriority.LOW, KubeJSForge::initRegistries);
		bus.addListener(EventPriority.LOW, KubeJSForge::commonSetup);
		bus.addListener(EventPriority.LOW, KubeJSForge::creativeTab);

		KubeJS.instance = new KubeJS();
		KubeJS.instance.setup();
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		NeoForge.EVENT_BUS.addListener(KubeJSForge::itemDestroyed);
		NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, KubeJSForge::livingDrops);

		if (!CommonProperties.get().serverOnly) {
			NeoForgeMod.enableMilkFluid();
			IngredientForgeHelper.register(bus);
			KubeJSCreativeTabs.init();
		}

		if (FMLEnvironment.dist == Dist.CLIENT) {
			new KubeJSForgeClient();
		}
	}

	private static void initRegistries(RegisterEvent event) {
		var info = RegistryInfo.of(event.getRegistryKey());
		info.registerObjects((id, supplier) -> event.register(UtilsJS.cast(info.key), id, supplier));
	}

	private static void commonSetup(FMLCommonSetupEvent event) {
	}

	private record CreativeTabCallbackForge(BuildCreativeModeTabContentsEvent event) implements CreativeTabCallback {
		@Override
		public void addAfter(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility) {
			for (var item : items) {
				event.accept(item, visibility);
			}
		}

		@Override
		public void addBefore(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility) {
			for (var item : items) {
				event.accept(item, visibility);
			}
		}

		@Override
		public void remove(Ingredient filter, boolean removeDisplay, boolean removeSearch) {
			var entries = new ArrayList<Map.Entry<ItemStack, CreativeModeTab.TabVisibility>>();

			for (var entry : event.getEntries()) {
				if (filter.test(entry.getKey())) {
					var visibility = entry.getValue();

					if (removeDisplay && removeSearch) {
						visibility = null;
					}

					if (removeDisplay && visibility == CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS) {
						visibility = CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY;
					}

					if (removeSearch && visibility == CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS) {
						visibility = CreativeModeTab.TabVisibility.PARENT_TAB_ONLY;
					}

					entries.add(new AbstractMap.SimpleEntry<>(entry.getKey(), visibility));
				}
			}

			for (var entry : entries) {
				if (entry.getValue() == null) {
					event.getEntries().remove(entry.getKey());
				} else {
					event.getEntries().put(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	private static void creativeTab(BuildCreativeModeTabContentsEvent event) {
		var tabId = event.getTabKey().location();

		if (StartupEvents.MODIFY_CREATIVE_TAB.hasListeners(tabId)) {
			StartupEvents.MODIFY_CREATIVE_TAB.post(ScriptType.STARTUP, tabId, new CreativeTabEvent(event.getTab(), event.hasPermissions(), new CreativeTabCallbackForge(event)));
		}
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
