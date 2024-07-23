package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class DynamicItemTooltipsKubeEvent implements KubeEvent {
	public final ItemStack item;
	public final List<Component> lines;
	public final boolean startup;
	public final boolean advanced;
	public final boolean creative;
	public final boolean shift;
	public final boolean ctrl;
	public final boolean alt;

	public DynamicItemTooltipsKubeEvent(ItemStack item, TooltipFlag flags, List<Component> lines, boolean startup) {
		this.item = item;
		this.lines = lines;
		this.startup = startup;
		this.advanced = flags.isAdvanced();
		this.creative = flags.isCreative();
		this.shift = !startup && Screen.hasShiftDown();
		this.ctrl = !startup && Screen.hasControlDown();
		this.alt = !startup && Screen.hasAltDown();
	}

	public void add(List<Component> text) {
		lines.addAll(text);
	}
}