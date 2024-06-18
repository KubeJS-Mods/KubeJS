package dev.latvian.mods.kubejs.integration.rei;

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.Arrays;

public class REIIntegration implements KubeJSPlugin {
	@Override
	public void registerEvents(EventGroupRegistry registry) {
		registry.register(REIEvents.GROUP);
	}

	public static EntryStack<?> stackOf(Context cx, RecipeViewerEntryType type, Object entry) {
		if (type == RecipeViewerEntryType.ITEM) {
			var in = (ItemStack) cx.jsToJava(entry, ItemStackJS.TYPE_INFO);
			return EntryStacks.of(in);
		} else if (type == RecipeViewerEntryType.FLUID) {
			var in = (FluidStack) cx.jsToJava(entry, FluidWrapper.TYPE_INFO);
			return EntryStacks.of(FluidStackHooksForge.fromForge(in));
		} else {
			((KubeJSContext) cx).getConsole().error("Currently custom type '" + type.id() + "' isn't supported");
			return EntryStack.empty();
		}
	}

	public static EntryIngredient ingredientOf(Context cx, RecipeViewerEntryType type, Object filter) {
		if (type == RecipeViewerEntryType.ITEM) {
			var in = (Ingredient) cx.jsToJava(filter, IngredientJS.TYPE_INFO);
			return EntryIngredients.ofIngredient(in);
		} else if (type == RecipeViewerEntryType.FLUID) {
			var in = (FluidIngredient) cx.jsToJava(filter, FluidWrapper.INGREDIENT_TYPE_INFO);
			return EntryIngredient.of(Arrays.stream(in.getStacks()).map(FluidStackHooksForge::fromForge).map(EntryStacks::of).toList());
		} else {
			((KubeJSContext) cx).getConsole().error("Currently custom type '" + type.id() + "' isn't supported");
			return EntryIngredient.empty();
		}
	}
}