package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

public record ReplaceAction(ItemStack item) implements IngredientAction {
	public static final IngredientActionType<ReplaceAction> TYPE = new IngredientActionType<>(KubeJS.id("replace"), RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemStack.CODEC.fieldOf("item").forGetter(ReplaceAction::item)
	).apply(instance, ReplaceAction::new)));

	@Override
	public IngredientActionType<?> getType() {
		return TYPE;
	}

	@Override
	public ItemStack transform(ItemStack old, int index, CraftingInput input) {
		return item.copy();
	}
}
