package dev.latvian.mods.kubejs.tooltip.action;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record InsertTooltipAction(int line, List<Component> lines) implements TooltipAction {
	public static final TooltipActionType<InsertTooltipAction> TYPE = new TooltipActionType<>(2, StreamCodec.composite(
		ByteBufCodecs.VAR_INT, InsertTooltipAction::line,
		ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()), InsertTooltipAction::lines,
		InsertTooltipAction::new
	));

	@Override
	public TooltipActionType<?> type() {
		return TYPE;
	}

	@Override
	public void apply(List<Component> tooltip) {
		tooltip.addAll(line, lines);
	}
}
