package dev.latvian.mods.kubejs.forge;

import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.entity.forge.LivingEntityDropsEventJS;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.item.forge.ItemDestroyedEventJS;

public interface ForgeKubeJSEvents {
	EventHandler ITEM_DESTROYED = KubeJSEvents.ITEM_GROUP.server("destroyed", () -> ItemDestroyedEventJS.class).legacy("item.destroyed");

	EventHandler ENTITY_DROPS = KubeJSEvents.ENTITY_GROUP.server("drops", () -> LivingEntityDropsEventJS.class).cancelable().legacy("entity.drops");

	static void register() {
	}
}
