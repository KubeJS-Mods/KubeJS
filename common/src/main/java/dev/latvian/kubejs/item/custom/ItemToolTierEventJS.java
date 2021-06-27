package dev.latvian.kubejs.item.custom;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.item.ModifiedToolTier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.Tiers;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ItemToolTierEventJS extends EventJS {
	public void add(String id, Consumer<ModifiedToolTier> tier) {
		ModifiedToolTier t = new ModifiedToolTier(Tiers.IRON);
		tier.accept(t);
		ItemBuilder.TOOL_TIERS.put(id, t);
	}

	public boolean isShift() {
		return Screen.hasShiftDown();
	}

	public boolean isCtrl() {
		return Screen.hasControlDown();
	}

	public boolean isAlt() {
		return Screen.hasAltDown();
	}
}