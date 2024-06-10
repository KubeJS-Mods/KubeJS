package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IngredientAction {
	Codec<IngredientAction> CODEC = IngredientActionType.CODEC.dispatch("action", IngredientAction::getType, IngredientActionType::codec);
	StreamCodec<RegistryFriendlyByteBuf, IngredientAction> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

	static ItemStack getRemaining(CraftingContainer container, int index, List<IngredientActionHolder> ingredientActions) {
		var stack = container.getItem(index);

		if (stack == null || stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		for (var holder : ingredientActions) {
			if (holder.filter().checkFilter(index, stack)) {
				return holder.action().transform(stack.copy(), index, container);
			}
		}

		if (stack.hasCraftingRemainingItem()) {
			return stack.getCraftingRemainingItem();
		}

		return ItemStack.EMPTY;
	}

	IngredientActionType getType();

	ItemStack transform(ItemStack old, int index, CraftingContainer container);
}
