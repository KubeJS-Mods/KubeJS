package dev.latvian.mods.kubejs.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.bindings.event.WorldgenEvents;
import dev.latvian.mods.kubejs.entity.forge.LivingEntityDropsEventJS;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
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
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

@Mod(KubeJS.MOD_ID)
public class KubeJSForge {
	public KubeJSForge() throws Throwable {
		var bus = FMLJavaModLoadingContext.get().getModEventBus();
		EventBuses.registerModEventBus(KubeJS.MOD_ID, bus);
		bus.addListener(EventPriority.LOW, KubeJSForge::loadComplete);
		bus.addListener(EventPriority.LOW, KubeJSForge::initRegistries);
		bus.addListener(EventPriority.LOW, KubeJSForge::commonSetup);
		bus.addListener(EventPriority.LOW, KubeJSForge::creativeTab);

		KubeJS.instance = new KubeJS();
		KubeJS.instance.setup();
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		MinecraftForge.EVENT_BUS.addListener(KubeJSForge::itemDestroyed);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, KubeJSForge::livingDrops);

		if (!CommonProperties.get().serverOnly) {
			ForgeMod.enableMilkFluid();
			IngredientForgeHelper.register();
			KubeJSCreativeTabs.init();
		}

		//noinspection Convert2MethodRef
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> new KubeJSForgeClient());
	}

	private static void initRegistries(RegisterEvent event) {
		var info = RegistryInfo.of(event.getRegistryKey());
		info.registerObjects((id, supplier) -> event.register(UtilsJS.cast(info.key), id, supplier));
		if (event.getRegistryKey() == ForgeRegistries.Keys.FLUID_TYPES) {
			for (var builder : RegistryInfo.FLUID) {
				if (builder instanceof FluidBuilder b) {
					Fluid f = ForgeRegistries.FLUIDS.getValue(b.id);
					event.register(ForgeRegistries.Keys.FLUID_TYPES, b.id, () -> f.getFluidType());
				}
			}
		}
	}

	private static void commonSetup(FMLCommonSetupEvent event) {
		WorldgenEvents.post();
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
