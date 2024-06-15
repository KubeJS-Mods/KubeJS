package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

import java.util.List;

public interface IngredientAction {
	Codec<IngredientAction> CODEC = IngredientActionType.CODEC.dispatch("type", IngredientAction::getType, IngredientActionType::codec);
	StreamCodec<RegistryFriendlyByteBuf, IngredientAction> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

	static ItemStack getRemaining(CraftingInput input, int index, List<IngredientActionHolder> ingredientActions) {
		var stack = input.getItem(index);

		if (stack == null || stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		for (var holder : ingredientActions) {
			if (holder.filter().checkFilter(index, stack)) {
				return holder.action().transform(stack.copy(), index, input);
			}
		}

		if (stack.hasCraftingRemainingItem()) {
			return stack.getCraftingRemainingItem();
		}

		return ItemStack.EMPTY;
	}

	IngredientActionType<?> getType();

	ItemStack transform(ItemStack old, int index, CraftingInput input);
}
