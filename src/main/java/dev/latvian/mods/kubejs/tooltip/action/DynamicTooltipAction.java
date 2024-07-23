package dev.latvian.mods.kubejs.tooltip.action;

import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.List;

public record DynamicTooltipAction(String id) implements TooltipAction {
	public static final TooltipActionType<DynamicTooltipAction> TYPE = new TooltipActionType<>(0, ByteBufCodecs.STRING_UTF8.map(DynamicTooltipAction::new, DynamicTooltipAction::id));

	@Override
	public TooltipActionType<?> type() {
		return TYPE;
	}

	@Override
	public void apply(List<Component> tooltip) {
		tooltip.add(Component.literal("Dynamic tooltip is not supported!").kjs$red());
	}
}
