package dev.latvian.mods.kubejs.tooltip.action;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;

import java.util.List;

public record RemoveTextTooltipAction(Component match) implements TooltipAction {
	public static final TooltipActionType<RemoveTextTooltipAction> TYPE = new TooltipActionType<>(4, ComponentSerialization.STREAM_CODEC.map(RemoveTextTooltipAction::new, RemoveTextTooltipAction::match));

	@Override
	public TooltipActionType<?> type() {
		return TYPE;
	}

	@Override
	public void apply(List<Component> tooltip) {
		tooltip.removeIf(component -> RemoveTextTooltipAction.equals(component, match.getContents()));
	}

	private static boolean equals(Component c, ComponentContents contents) {
		if (c.getContents().equals(contents)) {
			return true;
		}

		for (var s : c.getSiblings()) {
			if (equals(s, contents)) {
				return true;
			}
		}

		return false;
	}
}
