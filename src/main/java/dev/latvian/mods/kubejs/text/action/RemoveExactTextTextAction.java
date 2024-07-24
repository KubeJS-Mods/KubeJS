package dev.latvian.mods.kubejs.text.action;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import java.util.List;

public record RemoveExactTextTextAction(Component match) implements TextAction {
	public static final TooltipActionType<RemoveExactTextTextAction> TYPE = new TooltipActionType<>(5, ComponentSerialization.STREAM_CODEC.map(RemoveExactTextTextAction::new, RemoveExactTextTextAction::match));

	@Override
	public TooltipActionType<?> type() {
		return TYPE;
	}

	@Override
	public void apply(List<Component> lines) {
		lines.removeIf(match::equals);
	}
}
