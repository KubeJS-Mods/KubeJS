package dev.latvian.mods.kubejs.integration.rei;

import dev.architectury.event.EventResult;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.comparison.FluidComparatorRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@REIPluginClient
@SuppressWarnings("UnstableApiUsage")
public class KubeJSREIPlugin implements REIClientPlugin {
	private final Set<CategoryIdentifier<?>> categoriesRemoved = new HashSet<>();
	private final Map<CategoryIdentifier<?>, Collection<ResourceLocation>> recipesRemoved = new HashMap<>();

	/**
	 * We want to run as late as possible, so we can remove other
	 * mods' entries after they have already been added.
	 */
	@Override
	public double getPriority() {
		return 1e7;
	}

	@Override
	public void registerEntries(EntryRegistry registry) {
		for (var type : RecipeViewerEntryType.ALL_TYPES.get()) {
			var entryType = REIIntegration.typeOf(type);

			if (entryType != null && RecipeViewerEvents.ADD_ENTRIES.hasListeners(type)) {
				RecipeViewerEvents.ADD_ENTRIES.post(ScriptType.CLIENT, type, new REIAddEntriesKubeEvent(type, entryType, registry));
			}
		}
	}

	@Override
	public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {
		for (var type : RecipeViewerEntryType.ALL_TYPES.get()) {
			var entryType = REIIntegration.typeOf(type);

			if (entryType != null && RecipeViewerEvents.REMOVE_ENTRIES.hasListeners(type)) {
				var registry = EntryRegistry.getInstance();
				var allEntries = registry.getEntryStacks().filter(e -> e.getType() == entryType).toArray(EntryStack[]::new);
				RecipeViewerEvents.REMOVE_ENTRIES.post(ScriptType.CLIENT, type, new REIRemoveEntriesKubeEvent(type, registry, allEntries, rule));
			}
		}
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		for (var type : RecipeViewerEntryType.ALL_TYPES.get()) {
			if (RecipeViewerEvents.ADD_INFORMATION.hasListeners(type)) {
				RecipeViewerEvents.ADD_INFORMATION.post(ScriptType.CLIENT, type, new REIAddInformationKubeEvent(type));
			}
		}

		registry.registerVisibilityPredicate((cat, display) -> {
			var id = display.getDisplayLocation();
			if (id.isPresent() && recipesRemoved.getOrDefault(cat.getCategoryIdentifier(), List.of()).contains(id.get())) {
				return EventResult.interruptFalse();
			}

			return EventResult.pass();
		});
	}

	@Override
	public void registerCategories(CategoryRegistry registry) {
		registry.registerVisibilityPredicate(category -> categoriesRemoved.contains(category.getCategoryIdentifier()) ? EventResult.interruptFalse() : EventResult.pass());
	}

	@Override
	public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
		if (stage == ReloadStage.END) {
			categoriesRemoved.clear();
			recipesRemoved.clear();

			if (RecipeViewerEvents.REMOVE_CATEGORIES.hasListeners()) {
				RecipeViewerEvents.REMOVE_CATEGORIES.post(ScriptType.CLIENT, new REIRemoveCategoriesKubeEvent(categoriesRemoved));
			}

			if (RecipeViewerEvents.REMOVE_RECIPES.hasListeners()) {
				RecipeViewerEvents.REMOVE_RECIPES.post(ScriptType.CLIENT, new REIRemoveRecipeKubeEvent(recipesRemoved));
			}
		}
	}

	@Override
	public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
		for (var type : RecipeViewerEntryType.ALL_TYPES.get()) {
			var entryType = REIIntegration.typeOf(type);

			if (entryType != null && RecipeViewerEvents.GROUP_ENTRIES.hasListeners(type)) {
				RecipeViewerEvents.GROUP_ENTRIES.post(ScriptType.CLIENT, type, new REIGroupEntriesKubeEvent(type, entryType, registry));
			}
		}
	}

	@Override
	public void registerItemComparators(ItemComparatorRegistry registry) {
		if (RecipeViewerEvents.REGISTER_SUBTYPES.hasListeners(RecipeViewerEntryType.ITEM)) {
			RecipeViewerEvents.REGISTER_SUBTYPES.post(ScriptType.CLIENT, RecipeViewerEntryType.ITEM, new REIRegisterItemSubtypesKubeEvent(registry));
		}
	}

	@Override
	public void registerFluidComparators(FluidComparatorRegistry registry) {
		if (RecipeViewerEvents.REGISTER_SUBTYPES.hasListeners(RecipeViewerEntryType.FLUID)) {
			RecipeViewerEvents.REGISTER_SUBTYPES.post(ScriptType.CLIENT, RecipeViewerEntryType.FLUID, new REIRegisterFluidSubtypesKubeEvent(registry));
		}
	}
}