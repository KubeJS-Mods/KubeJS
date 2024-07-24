package dev.latvian.mods.kubejs.text.action;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.List;

public record AddTextAction(List<Component> lines) implements TextAction {
	public static final TooltipActionType<AddTextAction> TYPE = new TooltipActionType<>(1, ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()).map(AddTextAction::new, AddTextAction::lines));

	@Override
	public TooltipActionType<?> type() {
		return TYPE;
	}

	@Override
	public void apply(List<Component> lines) {
		lines.addAll(this.lines);
	}
}
