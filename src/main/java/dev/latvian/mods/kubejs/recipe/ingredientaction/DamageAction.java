package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

public record DamageAction(int damage) implements IngredientAction {
	public static final IngredientActionType<DamageAction> TYPE = new IngredientActionType<>(KubeJS.id("damage"), RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("damage").forGetter(DamageAction::damage)
	).apply(instance, DamageAction::new)));

	@Override
	public IngredientActionType<?> getType() {
		return TYPE;
	}

	@Override
	public ItemStack transform(ItemStack old, int index, CraftingInput input) {
		old.setDamageValue(old.getDamageValue() + damage);
		return old.getDamageValue() >= old.getMaxDamage() ? ItemStack.EMPTY : old;
	}
}
