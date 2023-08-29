package dev.latvian.mods.kubejs.core.mixin.forge;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin({CompoundIngredient.class, IntersectionIngredient.class})
public abstract class IngredientsWithChildrenMixin extends AbstractIngredient {
	@Shadow(remap = false)
	private List<Ingredient> children;

	@Override
	public boolean kjs$canBeUsedForMatching() {
		for (var child : children) {
			if (!child.kjs$canBeUsedForMatching()) {
				return false;
			}
		}

		return true;
	}
}
