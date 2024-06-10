package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public record ReplaceAction(ItemStack item) implements IngredientAction {
	public static final IngredientActionType TYPE = new IngredientActionType("replace", RecordCodecBuilder.<ReplaceAction>mapCodec(instance -> instance.group(
		ItemStack.CODEC.fieldOf("item").forGetter(ReplaceAction::item)
	).apply(instance, ReplaceAction::new)));

	@Override
	public IngredientActionType getType() {
		return TYPE;
	}

	@Override
	public ItemStack transform(ItemStack old, int index, CraftingContainer container) {
		return item.copy();
	}
}
