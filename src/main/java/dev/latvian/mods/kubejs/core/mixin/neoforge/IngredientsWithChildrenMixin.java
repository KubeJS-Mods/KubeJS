package dev.latvian.mods.kubejs.core.mixin.neoforge;

import dev.latvian.mods.kubejs.core.IngredientKJS;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ChildBasedIngredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ChildBasedIngredient.class)
public abstract class IngredientsWithChildrenMixin implements IngredientKJS {
	@Final
	@Shadow(remap = false)
	protected List<Ingredient> children;

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
