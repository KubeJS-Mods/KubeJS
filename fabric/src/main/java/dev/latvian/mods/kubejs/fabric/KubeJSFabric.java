package dev.latvian.mods.kubejs.fabric;

import com.mojang.serialization.Lifecycle;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.bindings.event.WorldgenEvents;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabCallback;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabEvent;
import dev.latvian.mods.kubejs.item.creativetab.KubeJSCreativeTabs;
import dev.latvian.mods.kubejs.platform.fabric.IngredientFabricHelper;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.HashSet;

public class KubeJSFabric implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer {
	@Override
	public void onInitialize() {
		try {
			KubeJS.instance = new KubeJS();
			KubeJS.instance.setup();
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}

		IngredientFabricHelper.register();
		ItemGroupEvents.MODIFY_ENTRIES_ALL.register(this::modifyCreativeTab);
		KubeJSCreativeTabs.init();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void registerObjects() {
		var ignored = new HashSet<>(RegistryInfo.AFTER_VANILLA);

		for (var info : RegistryInfo.MAP.values()) {
			final var key = (ResourceKey) info.key;

			if (!ignored.contains(info) && BuiltInRegistries.REGISTRY.get(key) instanceof WritableRegistry<?> reg) {
				info.registerObjects((id, obj) -> reg.register(ResourceKey.create(key, id), UtilsJS.cast(obj.get()), Lifecycle.stable()));
			}
		}

		for (var info : RegistryInfo.AFTER_VANILLA) {
			final var key = (ResourceKey) info.key;

			if (BuiltInRegistries.REGISTRY.get(key) instanceof WritableRegistry<?> reg) {
				info.registerObjects((id, obj) -> reg.register(ResourceKey.create(key, id), UtilsJS.cast(obj.get()), Lifecycle.stable()));
			}
		}
	}

	@Override
	public void onInitializeClient() {
		registerObjects();
		WorldgenEvents.post();
		KubeJS.instance.loadComplete();
		KubeJS.PROXY.clientSetup();
		clientRegistry();
	}

	private void clientRegistry() {
		KubeJSFabricClient.registry();
	}

	@Override
	public void onInitializeServer() {
		registerObjects();
		WorldgenEvents.post();
		KubeJS.instance.loadComplete();
	}

	private record CreativeTabCallbackFabric(FabricItemGroupEntries entries) implements CreativeTabCallback {
		@Override
		public void addAfter(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility) {
			entries.addAfter(order, Arrays.asList(items), visibility);
		}

		@Override
		public void addBefore(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility) {
			entries.addBefore(order, Arrays.asList(items), visibility);
		}

		@Override
		public void remove(Ingredient filter, boolean removeDisplay, boolean removeSearch) {
			if (removeDisplay) {
				entries.getDisplayStacks().removeIf(filter);
			}

			if (removeSearch) {
				entries.getSearchTabStacks().removeIf(filter);
			}
		}
	}

	private void modifyCreativeTab(CreativeModeTab tab, FabricItemGroupEntries entries) {
		var tabId = KubeJSRegistries.creativeModeTabs().getId(tab);

		if (StartupEvents.MODIFY_CREATIVE_TAB.hasListeners(tabId)) {
			StartupEvents.MODIFY_CREATIVE_TAB.post(ScriptType.STARTUP, tabId, new CreativeTabEvent(tab, entries.shouldShowOpRestrictedItems(), new CreativeTabCallbackFabric(entries)));
		}
	}
}
