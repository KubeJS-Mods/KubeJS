package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.CustomIngredientKJS;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.stream.Stream;

@Mixin(ICustomIngredient.class)
public interface ICustomIngredientMixin extends CustomIngredientKJS {
	@Shadow
	Ingredient toVanilla();

	@Shadow
	Stream<Holder<Item>> items();
}
