package dev.latvian.mods.kubejs.core.mixin.forge;

import dev.latvian.mods.kubejs.core.IngredientKJS;
import net.neoforged.neoforge.common.crafting.NBTIngredient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({NBTIngredient.class})
public abstract class TrivialIngredientsMixin implements IngredientKJS {
	@Override
	public boolean kjs$canBeUsedForMatching() {
		return true;
	}
}
