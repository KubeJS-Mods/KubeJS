package dev.latvian.mods.kubejs.text.action;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record InsertTextAction(int line, List<Component> lines) implements TextAction {
	public static final TooltipActionType<InsertTextAction> TYPE = new TooltipActionType<>(2, StreamCodec.composite(
		ByteBufCodecs.VAR_INT, InsertTextAction::line,
		ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()), InsertTextAction::lines,
		InsertTextAction::new
	));

	@Override
	public TooltipActionType<?> type() {
		return TYPE;
	}

	@Override
	public void apply(List<Component> lines) {
		lines.addAll(line, this.lines);
	}
}
