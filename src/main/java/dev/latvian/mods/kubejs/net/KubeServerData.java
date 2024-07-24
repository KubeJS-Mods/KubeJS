package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.item.ModifyItemTooltipsKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.server.RecipeViewerData;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.text.tooltip.ItemTooltipData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record KubeServerData(
	Optional<RecipeViewerData> recipeViewerData,
	List<ItemTooltipData> itemTooltipData
) {
	public static final StreamCodec<RegistryFriendlyByteBuf, KubeServerData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(RecipeViewerData.STREAM_CODEC), KubeServerData::recipeViewerData,
		ItemTooltipData.STREAM_CODEC.apply(ByteBufCodecs.list()), KubeServerData::itemTooltipData,
		KubeServerData::new
	);

	public static KubeServerData collect() {
		var itemTooltipData = new ArrayList<ItemTooltipData>();
		ItemEvents.MODIFY_TOOLTIPS.post(ScriptType.SERVER, new ModifyItemTooltipsKubeEvent(itemTooltipData::add));

		return new KubeServerData(
			Optional.ofNullable(RecipeViewerData.collect()),
			List.copyOf(itemTooltipData)
		);
	}
}
