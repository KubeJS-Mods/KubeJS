package dev.latvian.mods.kubejs.tooltip.action;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.List;

public record AddTooltipAction(List<Component> lines) implements TooltipAction {
	public static final TooltipActionType<AddTooltipAction> TYPE = new TooltipActionType<>(1, ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()).map(AddTooltipAction::new, AddTooltipAction::lines));

	@Override
	public TooltipActionType<?> type() {
		return TYPE;
	}

	@Override
	public void apply(List<Component> tooltip) {
		tooltip.addAll(lines);
	}
}
