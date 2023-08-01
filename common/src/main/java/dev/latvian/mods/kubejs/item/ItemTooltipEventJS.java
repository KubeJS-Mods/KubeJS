package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.bindings.TextWrapper;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Info("""
	Invoked when registering handlers for item tooltips.
			
	`text` can be a component or a list of components.
	""")
public class ItemTooltipEventJS extends EventJS {
	@FunctionalInterface
	public interface StaticTooltipHandler {
		void tooltip(ItemStack stack, boolean advanced, List<Component> components);
	}

	@FunctionalInterface
	public interface StaticTooltipHandlerFromJS {
		void accept(ItemStack stack, boolean advanced, List<Object> text);
	}

	public static class StaticTooltipHandlerFromLines implements StaticTooltipHandler {
		public final List<Component> lines;

		public StaticTooltipHandlerFromLines(List<Component> l) {
			lines = l;
		}

		public StaticTooltipHandlerFromLines(Object o) {
			lines = new ArrayList<>();

			for (var o1 : ListJS.orSelf(o)) {
				lines.add(TextWrapper.of(o1));
			}
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

			List<Object> text = new ArrayList<>(components);

			try {
				handler.accept(stack, advanced, text);
			} catch (Exception ex) {
				ConsoleJS.CLIENT.error("Error while gathering tooltip for " + stack, ex);
			}

			components.clear();

			for (var o : text) {
				components.add(TextWrapper.of(o));
			}
		}
	}

	private final Map<Item, List<StaticTooltipHandler>> map;

	public ItemTooltipEventJS(Map<Item, List<ItemTooltipEventJS.StaticTooltipHandler>> m) {
		map = m;
	}

	@Info("Adds text to all items matching the ingredient.")
	public void add(Ingredient item, Object text) {
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
	public void addToAll(Object text) {
		var l = new StaticTooltipHandlerFromLines(text);

		if (!l.lines.isEmpty()) {
			map.computeIfAbsent(Items.AIR, k -> new ArrayList<>()).add(l);
		}
	}

	@Info("Adds a dynamic tooltip handler to all items matching the ingredient.")
	public void addAdvanced(Ingredient item, StaticTooltipHandlerFromJS handler) {
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