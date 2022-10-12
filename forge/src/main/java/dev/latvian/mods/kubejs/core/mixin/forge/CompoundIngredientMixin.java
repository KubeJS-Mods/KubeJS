package dev.latvian.mods.kubejs.core.mixin.forge;

import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CompoundIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Set;

@Mixin(value = CompoundIngredient.class, remap = false)
public abstract class CompoundIngredientMixin extends AbstractIngredient implements IngredientKJS {
	@Shadow
	private List<Ingredient> children;

	@Override
	public void kjs$gatherStacks(ItemStackSet set) {
		for (var in : children) {
			in.kjs$gatherStacks(set);
		}
	}

	@Override
	public void kjs$gatherItemTypes(Set<Item> set) {
		for (var in : children) {
			in.kjs$gatherItemTypes(set);
		}
	}

	@Override
	public ItemStack kjs$getFirst() {
		for (var in : children) {
			var stack = in.kjs$getFirst();

			if (!stack.isEmpty()) {
				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public Ingredient kjs$or(Ingredient ingredient) {
		if (ingredient != Ingredient.EMPTY) {
			Ingredient[] in = new Ingredient[children.size() + 1];
			System.arraycopy(children.toArray(new Ingredient[0]), 0, in, 0, children.size());
			in[children.size()] = ingredient;
			return CompoundIngredient.of(in);
		}

		return this;
	}
}
