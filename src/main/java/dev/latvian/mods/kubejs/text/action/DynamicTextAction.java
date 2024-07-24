package dev.latvian.mods.kubejs.text.action;

import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.List;

public record DynamicTextAction(String id) implements TextAction {
	public static final TooltipActionType<DynamicTextAction> TYPE = new TooltipActionType<>(0, ByteBufCodecs.STRING_UTF8.map(DynamicTextAction::new, DynamicTextAction::id));

	@Override
	public TooltipActionType<?> type() {
		return TYPE;
	}

	@Override
	public void apply(List<Component> lines) {
		lines.add(Component.literal("Not supported!").kjs$red());
	}
}
