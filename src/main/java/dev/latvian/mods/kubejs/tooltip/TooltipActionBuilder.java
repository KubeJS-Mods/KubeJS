package dev.latvian.mods.kubejs.tooltip;

import dev.latvian.mods.kubejs.tooltip.action.AddTooltipAction;
import dev.latvian.mods.kubejs.tooltip.action.DynamicTooltipAction;
import dev.latvian.mods.kubejs.tooltip.action.InsertTooltipAction;
import dev.latvian.mods.kubejs.tooltip.action.RemoveExactTextTooltipAction;
import dev.latvian.mods.kubejs.tooltip.action.RemoveLineTooltipAction;
import dev.latvian.mods.kubejs.tooltip.action.RemoveTextTooltipAction;
import dev.latvian.mods.kubejs.tooltip.action.TooltipAction;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class TooltipActionBuilder {
	@HideFromJS
	public List<TooltipAction> actions = new ArrayList<>(1);

	public void dynamic(String id) {
		actions.add(new DynamicTooltipAction(id));
	}

	public void add(List<Component> text) {
		actions.add(new AddTooltipAction(text));
	}

	public void insert(int line, List<Component> text) {
		actions.add(new InsertTooltipAction(line, text));
	}

	public void removeLine(int line) {
		actions.add(new RemoveLineTooltipAction(line));
	}

	public void removeText(Component match) {
		actions.add(new RemoveTextTooltipAction(match));
	}

	public void removeExactText(Component match) {
		actions.add(new RemoveExactTextTooltipAction(match));
	}
}
