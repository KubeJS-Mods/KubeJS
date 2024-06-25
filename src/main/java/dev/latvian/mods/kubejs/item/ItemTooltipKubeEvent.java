package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Info("""
	Invoked when registering handlers for item tooltips.
			
	`text` can be a component or a list of components.
	""")
public class ItemTooltipKubeEvent implements KubeEvent {
	@FunctionalInterface
	public interface StaticTooltipHandler {
		void tooltip(ItemStack stack, boolean advanced, List<Component> components);
	}

	@FunctionalInterface
	public interface StaticTooltipHandlerFromJS {
		void accept(ItemStack stack, boolean advanced, List<Component> text);
	}

	public static class StaticTooltipHandlerFromLines implements StaticTooltipHandler {
		public final List<Component> lines;

		public StaticTooltipHandlerFromLines(List<Component> l) {
			lines = l;
		}

		@Override
		public void tooltip(ItemStack stack, boolean advanced, List<Component> components) {
			if (!stack.isEmpty()) {
				components.addAll(lines);
			}
		}
	}

	public static class StaticTooltipHandlerFromJSWrapper implements StaticTooltipHandler {
		private final StaticTooltipHandlerFromJS handler;

		public StaticTooltipHandlerFromJSWrapper(StaticTooltipHandlerFromJS h) {
			handler = h;
		}

		@Override
		public void tooltip(ItemStack stack, boolean advanced, List<Component> components) {
			if (stack.isEmpty()) {
				return;
			}

			List<Component> text = new ArrayList<>(components);

			try {
				handler.accept(stack, advanced, text);
			} catch (Exception ex) {
				ConsoleJS.CLIENT.error("Error while gathering tooltip for " + stack, ex);
			}

			components.clear();
			components.addAll(text);
		}
	}

	private final Map<Item, List<StaticTooltipHandler>> map;

	public ItemTooltipKubeEvent(Map<Item, List<ItemTooltipKubeEvent.StaticTooltipHandler>> m) {
		map = m;
	}

	@Info("Adds text to all items matching the ingredient.")
	public void add(ItemPredicate item, List<Component> text) {
		if (item.kjs$isWildcard()) {
			addToAll(text);
			return;
		}

		var l = new StaticTooltipHandlerFromLines(text);

		if (!l.lines.isEmpty()) {
			for (var i : item.kjs$getItemTypes()) {
				if (i != Items.AIR) {
					map.computeIfAbsent(i, k -> new ArrayList<>()).add(l);
				}
			}
		}
	}

	@Info("Adds text to all items.")
	public void addToAll(List<Component> text) {
		var l = new StaticTooltipHandlerFromLines(text);

		if (!l.lines.isEmpty()) {
			map.computeIfAbsent(Items.AIR, k -> new ArrayList<>()).add(l);
		}
	}

	@Info("Adds a dynamic tooltip handler to all items matching the ingredient.")
	public void addAdvanced(ItemPredicate item, StaticTooltipHandlerFromJS handler) {
		if (item.kjs$isWildcard()) {
			addAdvancedToAll(handler);
			return;
		}

		var l = new StaticTooltipHandlerFromJSWrapper(handler);

		for (var i : item.kjs$getItemTypes()) {
			if (i != Items.AIR) {
				map.computeIfAbsent(i, k -> new ArrayList<>()).add(l);
			}
		}
	}

	@Info("Adds a dynamic tooltip handler to all items.")
	public void addAdvancedToAll(StaticTooltipHandlerFromJS handler) {
		map.computeIfAbsent(Items.AIR, k -> new ArrayList<>()).add(new StaticTooltipHandlerFromJSWrapper(handler));
	}

	@Info("Is shift key pressed.")
	public boolean isShift() {
		return Screen.hasShiftDown();
	}

	@Info("Is control key pressed.")
	public boolean isCtrl() {
		return Screen.hasControlDown();
	}

	@Info("Is alt key pressed.")
	public boolean isAlt() {
		return Screen.hasAltDown();
	}
}