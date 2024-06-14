package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

public record DamageAction(int damage) implements IngredientAction {
	public static final IngredientActionType TYPE = new IngredientActionType("damage", RecordCodecBuilder.<DamageAction>mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("damage").forGetter(DamageAction::damage)
	).apply(instance, DamageAction::new)));

	@Override
	public IngredientActionType getType() {
		return TYPE;
	}

	@Override
	public ItemStack transform(ItemStack old, int index, CraftingInput input) {
		old.setDamageValue(old.getDamageValue() + damage);
		return old.getDamageValue() >= old.getMaxDamage() ? ItemStack.EMPTY : old;
	}
}
