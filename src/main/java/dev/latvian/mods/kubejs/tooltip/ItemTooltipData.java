package dev.latvian.mods.kubejs.tooltip;

import dev.latvian.mods.kubejs.tooltip.action.TooltipAction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

public record ItemTooltipData(
	Optional<Ingredient> filter,
	Optional<TooltipRequirements> requirements,
	List<TooltipAction> actions
) {
	public static final StreamCodec<RegistryFriendlyByteBuf, ItemTooltipData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), ItemTooltipData::filter,
		ByteBufCodecs.optional(TooltipRequirements.STREAM_CODEC), ItemTooltipData::requirements,
		TooltipAction.STREAM_CODEC.apply(ByteBufCodecs.list()), ItemTooltipData::actions,
		ItemTooltipData::new
	);
}
