package dev.latvian.mods.kubejs.tooltip.action;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import java.util.List;

public record RemoveExactTextTooltipAction(Component match) implements TooltipAction {
	public static final TooltipActionType<RemoveExactTextTooltipAction> TYPE = new TooltipActionType<>(5, ComponentSerialization.STREAM_CODEC.map(RemoveExactTextTooltipAction::new, RemoveExactTextTooltipAction::match));

	@Override
	public TooltipActionType<?> type() {
		return TYPE;
	}

	@Override
	public void apply(List<Component> tooltip) {
		tooltip.removeIf(match::equals);
	}
}
