package dev.latvian.mods.kubejs.core.mixin.forge;

import dev.latvian.mods.kubejs.core.IngredientKJS;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.IntersectionIngredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin({CompoundIngredient.class, IntersectionIngredient.class})
public abstract class IngredientsWithChildrenMixin implements IngredientKJS {
	@Final
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
