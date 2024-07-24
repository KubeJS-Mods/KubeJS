package dev.latvian.mods.kubejs.text.action;

import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.List;

public record RemoveLineTextAction(int line) implements TextAction {
	public static final TooltipActionType<RemoveLineTextAction> TYPE = new TooltipActionType<>(3, ByteBufCodecs.VAR_INT.map(RemoveLineTextAction::new, RemoveLineTextAction::line));

	@Override
	public TooltipActionType<?> type() {
		return TYPE;
	}

	@Override
	public void apply(List<Component> lines) {
		if (line >= 0 && line < lines.size()) {
			lines.remove(line);
		}
	}
}
