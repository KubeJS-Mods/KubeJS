package dev.latvian.mods.kubejs.tooltip.action;

import dev.latvian.mods.kubejs.util.Cast;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface TooltipAction {
	Map<Integer, TooltipActionType<?>> MAP = Stream.of(
		DynamicTooltipAction.TYPE,
		AddTooltipAction.TYPE,
		InsertTooltipAction.TYPE,
		RemoveLineTooltipAction.TYPE,
		RemoveTextTooltipAction.TYPE,
		RemoveExactTextTooltipAction.TYPE
	).collect(Collectors.toMap(TooltipActionType::type, Function.identity()));

	StreamCodec<RegistryFriendlyByteBuf, TooltipAction> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public TooltipAction decode(RegistryFriendlyByteBuf buf) {
			int id = buf.readByte();
			return MAP.get(id).streamCodec().decode(buf);
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, TooltipAction value) {
			buf.writeByte(value.type().type());
			value.type().streamCodec().encode(buf, Cast.to(value));
		}
	};

	TooltipActionType<?> type();

	void apply(List<Component> tooltip);
}
