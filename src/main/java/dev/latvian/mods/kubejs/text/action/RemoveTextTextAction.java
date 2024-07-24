package dev.latvian.mods.kubejs.text.action;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;

import java.util.List;

public record RemoveTextTextAction(Component match) implements TextAction {
	public static final TooltipActionType<RemoveTextTextAction> TYPE = new TooltipActionType<>(4, ComponentSerialization.STREAM_CODEC.map(RemoveTextTextAction::new, RemoveTextTextAction::match));

	@Override
	public TooltipActionType<?> type() {
		return TYPE;
	}

	@Override
	public void apply(List<Component> lines) {
		lines.removeIf(component -> RemoveTextTextAction.equals(component, match.getContents()));
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
