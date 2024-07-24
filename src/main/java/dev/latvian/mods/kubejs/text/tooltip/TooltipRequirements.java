package dev.latvian.mods.kubejs.text.tooltip;

import dev.latvian.mods.kubejs.util.Tristate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

public record TooltipRequirements(
	Tristate shift,
	Tristate ctrl,
	Tristate alt,
	Tristate advanced,
	Tristate creative,
	Map<String, Tristate> stages
) {
	public static final TooltipRequirements DEFAULT = new TooltipRequirements(
		Tristate.DEFAULT,
		Tristate.DEFAULT,
		Tristate.DEFAULT,
		Tristate.DEFAULT,
		Tristate.DEFAULT,
		Map.of()
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, TooltipRequirements> STREAM_CODEC = StreamCodec.composite(
		Tristate.STREAM_CODEC, TooltipRequirements::shift,
		Tristate.STREAM_CODEC, TooltipRequirements::ctrl,
		Tristate.STREAM_CODEC, TooltipRequirements::alt,
		Tristate.STREAM_CODEC, TooltipRequirements::advanced,
		Tristate.STREAM_CODEC, TooltipRequirements::creative,
		ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, Tristate.STREAM_CODEC), TooltipRequirements::stages,
		TooltipRequirements::new
	);
}
