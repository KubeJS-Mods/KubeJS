package dev.latvian.mods.kubejs.integration.jei;
/*

import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.rhino.Context;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class JEIIntegration {
	@Nullable
	public static IIngredientType<?> typeOf(RecipeViewerEntryType type) {
		if (type == RecipeViewerEntryType.ITEM) {
			return VanillaTypes.ITEM_STACK;
		} else if (type == RecipeViewerEntryType.FLUID) {
			return NeoForgeTypes.FLUID_STACK;
		} else {
			return null;
		}
	}

	public static Object[] getEntries(RecipeViewerEntryType type, Context cx, Object filter) {
		if (type == RecipeViewerEntryType.ITEM) {
			return ((ItemPredicate) type.wrapPredicate(cx, filter)).kjs$getStackArray();
		} else if (type == RecipeViewerEntryType.FLUID) {
			return new List[]{((FluidIngredient) type.wrapPredicate(cx, filter)).fluids()};
		} else {
			return new Object[0];
		}
	}
}*/
