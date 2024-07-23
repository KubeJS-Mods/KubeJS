package dev.latvian.mods.kubejs.tooltip.action;

import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.List;

public record RemoveLineTooltipAction(int line) implements TooltipAction {
	public static final TooltipActionType<RemoveLineTooltipAction> TYPE = new TooltipActionType<>(3, ByteBufCodecs.VAR_INT.map(RemoveLineTooltipAction::new, RemoveLineTooltipAction::line));

	@Override
	public TooltipActionType<?> type() {
		return TYPE;
	}

	@Override
	public void apply(List<Component> tooltip) {
		tooltip.remove(line);
	}
}
