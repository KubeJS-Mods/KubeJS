package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEvents;
import dev.latvian.mods.kubejs.recipe.viewer.server.RecipeViewerData;
import dev.latvian.mods.kubejs.recipe.viewer.server.RemoteRecipeViewerDataUpdatedEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.CompoundFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@JeiPlugin
public class KubeJSJEIPlugin implements IModPlugin {
	public static final ResourceLocation ID = KubeJS.id("jei");
	public static final boolean DISABLED = ModList.get().isLoaded("emi");
	private RecipeViewerData remote = null;

	public KubeJSJEIPlugin() {
		NeoForge.EVENT_BUS.register(this);
	}

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@SubscribeEvent
	public void loadRemote(RemoteRecipeViewerDataUpdatedEvent event) {
		remote = event.data;
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime runtime) {
		if (DISABLED) {
			return;
		}

		var recipeManager = runtime.getRecipeManager();
		var ingredientManager = runtime.getIngredientManager();

		var categories = new HashMap<>(runtime.getRecipeManager().createRecipeCategoryLookup()
			.get()
			.collect(Collectors.toMap(cat -> cat.getRecipeType().getUid(), Function.identity())));

		if (RecipeViewerEvents.REMOVE_CATEGORIES.hasListeners()) {
			RecipeViewerEvents.REMOVE_CATEGORIES.post(ScriptType.CLIENT, new JEIRemoveCategoriesKubeEvent(runtime.getRecipeManager(), categories));
		}

		if (RecipeViewerEvents.REMOVE_RECIPES.hasListeners()) {
			RecipeViewerEvents.REMOVE_RECIPES.post(ScriptType.CLIENT, new JEIRemoveRecipesKubeEvent(runtime.getRecipeManager(), categories));
		}

		if (remote != null) {
			for (var removedCategory : remote.removedCategories()) {
				var category = categories.get(removedCategory);

				if (category != null) {
					recipeManager.hideRecipeCategory(category.getRecipeType());
					categories.remove(removedCategory);
				}
			}
		}

		var allItems = ingredientManager.getAllIngredients(VanillaTypes.ITEM_STACK);
		var allFluids = ingredientManager.getAllIngredients(NeoForgeTypes.FLUID_STACK);

		for (var type : RecipeViewerEntryType.ALL_TYPES.get()) {
			var ingredientType = JEIIntegration.typeOf(type);

			if (ingredientType != null && RecipeViewerEvents.REMOVE_ENTRIES.hasListeners(type)) {
				RecipeViewerEvents.REMOVE_ENTRIES.post(ScriptType.CLIENT, type, new JEIRemoveEntriesKubeEvent(runtime, type, ingredientType));
			}

			if (ingredientType != null && RecipeViewerEvents.REMOVE_ENTRIES_COMPLETELY.hasListeners(type)) {
				RecipeViewerEvents.REMOVE_ENTRIES_COMPLETELY.post(ScriptType.CLIENT, type, new JEIRemoveEntriesKubeEvent(runtime, type, ingredientType));
			}
		}

		if (remote != null) {
			// Item

			if (!remote.itemData().removedEntries().isEmpty() || !remote.itemData().completelyRemovedEntries().isEmpty()) {
				var filterList = new ArrayList<Ingredient>(remote.itemData().removedEntries().size() + remote.itemData().completelyRemovedEntries().size());
				filterList.addAll(remote.itemData().removedEntries());
				filterList.addAll(remote.itemData().completelyRemovedEntries());
				var filter = CompoundIngredient.of(filterList.toArray(new Ingredient[0]));
				var removed = new ArrayList<ItemStack>();

				for (var stack : allItems) {
					if (filter.test(stack)) {
						removed.add(stack);
					}
				}

				ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, removed);
			}

			// Fluid

			if (!remote.fluidData().removedEntries().isEmpty() || !remote.fluidData().completelyRemovedEntries().isEmpty()) {
				var filterList = new ArrayList<FluidIngredient>(remote.fluidData().removedEntries().size() + remote.fluidData().completelyRemovedEntries().size());
				filterList.addAll(remote.fluidData().removedEntries());
				filterList.addAll(remote.fluidData().completelyRemovedEntries());
				var filter = new CompoundFluidIngredient(filterList);
				var removed = new ArrayList<FluidStack>();

				for (var stack : allFluids) {
					if (filter.test(stack)) {
						removed.add(stack);
					}
				}

				ingredientManager.removeIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, removed);
			}
		}

		for (var type : RecipeViewerEntryType.ALL_TYPES.get()) {
			var ingredientType = JEIIntegration.typeOf(type);

			if (ingredientType != null && RecipeViewerEvents.ADD_ENTRIES.hasListeners(type)) {
				RecipeViewerEvents.ADD_ENTRIES.post(ScriptType.CLIENT, type, new JEIAddEntriesKubeEvent(runtime, type, ingredientType));
			}
		}

		if (remote != null) {
			// Item

			if (!remote.itemData().addedEntries().isEmpty()) {
				ingredientManager.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, remote.itemData().addedEntries());
			}

			// Fluid

			if (!remote.fluidData().addedEntries().isEmpty()) {
				ingredientManager.addIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, remote.fluidData().addedEntries());
			}
		}
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (DISABLED) {
			return;
		}

		for (var type : RecipeViewerEntryType.ALL_TYPES.get()) {
			var ingredientType = JEIIntegration.typeOf(type);

			if (ingredientType != null && RecipeViewerEvents.ADD_INFORMATION.hasListeners(type)) {
				RecipeViewerEvents.ADD_INFORMATION.post(ScriptType.CLIENT, type, new JEIAddInformationKubeEvent(type, ingredientType, registration));
			}
		}

		if (remote != null) {
			var allItems = registration.getIngredientManager().getAllIngredients(VanillaTypes.ITEM_STACK);

			for (var info : remote.itemData().info()) {
				var stacks = new ArrayList<ItemStack>();

				for (var stack : allItems) {
					if (info.filter().test(stack)) {
						stacks.add(stack);
					}
				}

				registration.addIngredientInfo(stacks, VanillaTypes.ITEM_STACK, info.info().toArray(new Component[0]));
			}

			var allFluids = registration.getIngredientManager().getAllIngredients(NeoForgeTypes.FLUID_STACK);

			for (var info : remote.fluidData().info()) {
				var stacks = new ArrayList<FluidStack>();

				for (var stack : allFluids) {
					if (info.filter().test(stack)) {
						stacks.add(stack);
					}
				}

				registration.addIngredientInfo(stacks, NeoForgeTypes.FLUID_STACK, info.info().toArray(new Component[0]));
			}
		}
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		if (DISABLED) {
			return;
		}

		if (RecipeViewerEvents.REGISTER_SUBTYPES.hasListeners(RecipeViewerEntryType.ITEM)) {
			RecipeViewerEvents.REGISTER_SUBTYPES.post(ScriptType.CLIENT, RecipeViewerEntryType.ITEM, new JEIRegisterSubtypesKubeEvent(RecipeViewerEntryType.ITEM, VanillaTypes.ITEM_STACK, registration));
		}

		if (remote != null) {
			for (var subtypes : remote.itemData().dataComponentSubtypes()) {
				var in = DataComponentTypeInterpreter.of(subtypes.components());

				for (var item : subtypes.filter().kjs$getItemTypes()) {
					registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item, in);
				}
			}
		}
	}

	@Override
	public <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> platformFluidHelper) {
		if (DISABLED) {
			return;
		}

		if (RecipeViewerEvents.REGISTER_SUBTYPES.hasListeners(RecipeViewerEntryType.FLUID)) {
			RecipeViewerEvents.REGISTER_SUBTYPES.post(ScriptType.CLIENT, RecipeViewerEntryType.FLUID, new JEIRegisterSubtypesKubeEvent(RecipeViewerEntryType.FLUID, NeoForgeTypes.FLUID_STACK, registration));
		}

		if (remote != null) {
			for (var subtypes : remote.fluidData().dataComponentSubtypes()) {
				var in = DataComponentTypeInterpreter.of(subtypes.components());

				for (var fluid : Arrays.stream(subtypes.filter().getStacks()).map(FluidStack::getFluid).toArray(Fluid[]::new)) {
					registration.registerSubtypeInterpreter(NeoForgeTypes.FLUID_STACK, fluid, in);
				}
			}
		}
	}
}