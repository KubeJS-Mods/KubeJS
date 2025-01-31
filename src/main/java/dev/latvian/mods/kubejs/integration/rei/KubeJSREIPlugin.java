package dev.latvian.mods.kubejs.integration.rei;

import dev.architectury.event.EventResult;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import dev.latvian.mods.kubejs.plugin.builtin.event.RecipeViewerEvents;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.server.RecipeViewerData;
import dev.latvian.mods.kubejs.recipe.viewer.server.RemoteRecipeViewerDataUpdatedEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.comparison.EntryComparator;
import me.shedaniel.rei.api.common.entry.comparison.FluidComparatorRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.client.BuiltinClientPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Arrays;
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
	private RecipeViewerData remote = null;

	public KubeJSREIPlugin() {
		NeoForge.EVENT_BUS.register(this);
	}

	/**
	 * We want to run as late as possible, so we can remove other
	 * mods' entries after they have already been added.
	 */
	@Override
	public double getPriority() {
		return 1e7;
	}

	@SubscribeEvent
	public void loadRemote(RemoteRecipeViewerDataUpdatedEvent event) {
		remote = event.data;
	}

	@Override
	public void registerEntries(EntryRegistry registry) {
		for (var type : RecipeViewerEntryType.ALL_TYPES.get()) {
			var entryType = REIIntegration.typeOf(type);

			if (entryType != null && RecipeViewerEvents.ADD_ENTRIES.hasListeners(type)) {
				RecipeViewerEvents.ADD_ENTRIES.post(ScriptType.CLIENT, type, new REIAddEntriesKubeEvent(type, entryType, registry));
			}
		}

		if (remote != null) {
			for (var stack : remote.itemData().addedEntries()) {
				registry.addEntries(EntryStacks.of(stack));
			}

			for (var stack : remote.fluidData().addedEntries()) {
				registry.addEntries(EntryStacks.of(FluidStackHooksForge.fromForge(stack)));
			}
		}
	}

	@Override
	public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {
		for (var type : RecipeViewerEntryType.ALL_TYPES.get()) {
			var entryType = REIIntegration.typeOf(type);

			if (entryType != null && RecipeViewerEvents.REMOVE_ENTRIES.hasListeners(type)) {
				var registry = EntryRegistry.getInstance();
				var allItems = registry.getEntryStacks().filter(e -> e.getType() == entryType).toList();
				RecipeViewerEvents.REMOVE_ENTRIES.post(ScriptType.CLIENT, type, new REIRemoveEntriesKubeEvent(type, registry, allItems));
			}

			if (entryType != null && RecipeViewerEvents.REMOVE_ENTRIES_COMPLETELY.hasListeners(type)) {
				var registry = EntryRegistry.getInstance();
				var allFluids = registry.getEntryStacks().filter(e -> e.getType() == entryType).toList();
				RecipeViewerEvents.REMOVE_ENTRIES_COMPLETELY.post(ScriptType.CLIENT, type, new REIRemoveEntriesCompletelyKubeEvent(type, allFluids, rule));
			}
		}

		if (remote != null) {
			var registry = EntryRegistry.getInstance();

			if (!remote.itemData().removedEntries().isEmpty() || remote.itemData().completelyRemovedEntries().isEmpty()) {
				var allItems = registry.getEntryStacks().filter(e -> e.getType() == VanillaEntryTypes.ITEM).toList();

				for (var filter : remote.itemData().removedEntries()) {
					for (var entry : allItems) {
						if (filter.test((ItemStack) entry.getValue())) {
							registry.removeEntry(entry);
						}
					}
				}

				for (var filter : remote.itemData().completelyRemovedEntries()) {
					rule.hide(allItems.stream().filter(e -> filter.test((ItemStack) e.getValue())).toList());
				}
			}

			if (!remote.fluidData().removedEntries().isEmpty() || remote.fluidData().completelyRemovedEntries().isEmpty()) {
				var allFluids = registry.getEntryStacks().filter(e -> e.getType() == VanillaEntryTypes.FLUID).toList();

				for (var filter : remote.fluidData().removedEntries()) {
					for (var entry : allFluids) {
						if (filter.test(FluidStackHooksForge.toForge((dev.architectury.fluid.FluidStack) entry.getValue()))) {
							registry.removeEntry(entry);
						}
					}
				}

				for (var filter : remote.fluidData().completelyRemovedEntries()) {
					rule.hide(allFluids.stream().filter(e -> filter.test(FluidStackHooksForge.toForge((dev.architectury.fluid.FluidStack) e.getValue()))).toList());
				}
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

		if (remote != null) {
			for (var info : remote.itemData().info()) {
				if (!info.filter().isEmpty() && !info.info().isEmpty()) {
					BuiltinClientPlugin.getInstance().registerInformation(EntryIngredients.ofIngredient(info.filter()), info.info().getFirst(), components -> {
						for (int i = 1; i < info.info().size(); i++) {
							components.add(info.info().get(i));
						}

						return components;
					});
				}
			}

			for (var info : remote.fluidData().info()) {
				if (!info.filter().isEmpty() && !info.info().isEmpty()) {
					BuiltinClientPlugin.getInstance().registerInformation(REIIntegration.fluidIngredient(info.filter()), info.info().getFirst(), components -> {
						for (int i = 1; i < info.info().size(); i++) {
							components.add(info.info().get(i));
						}

						return components;
					});
				}
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

			if (remote != null) {
				categoriesRemoved.addAll(remote.removedCategories().stream().map(CategoryIdentifier::of).toList());

				for (var entry : remote.categoryData()) {
					recipesRemoved.computeIfAbsent(CategoryIdentifier.of(entry.category()), k -> new HashSet<>()).addAll(entry.removedRecipes());
				}
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

		if (remote != null) {
			for (var group : remote.itemData().groupedEntries()) {
				registry.group(group.groupId(), group.description(), e -> e.getType() == VanillaEntryTypes.ITEM && group.filter().test((ItemStack) e.getValue()));
			}

			for (var group : remote.fluidData().groupedEntries()) {
				registry.group(group.groupId(), group.description(), e -> e.getType() == VanillaEntryTypes.FLUID && group.filter().test(FluidStackHooksForge.toForge((dev.architectury.fluid.FluidStack) e.getValue())));
			}
		}
	}

	@Override
	public void registerItemComparators(ItemComparatorRegistry registry) {
		if (RecipeViewerEvents.REGISTER_SUBTYPES.hasListeners(RecipeViewerEntryType.ITEM)) {
			RecipeViewerEvents.REGISTER_SUBTYPES.post(ScriptType.CLIENT, RecipeViewerEntryType.ITEM, new REIRegisterItemSubtypesKubeEvent(registry));
		}

		if (remote != null) {
			for (var subtypes : remote.itemData().dataComponentSubtypes()) {
				var items = subtypes.filter().kjs$getItemTypes().toArray(new Item[0]);

				if (subtypes.components().isEmpty()) {
					registry.registerComponents(items);
				} else {
					registry.register((EntryComparator) DataComponentComparator.of(subtypes.components()), items);
				}
			}
		}
	}

	@Override
	public void registerFluidComparators(FluidComparatorRegistry registry) {
		if (RecipeViewerEvents.REGISTER_SUBTYPES.hasListeners(RecipeViewerEntryType.FLUID)) {
			RecipeViewerEvents.REGISTER_SUBTYPES.post(ScriptType.CLIENT, RecipeViewerEntryType.FLUID, new REIRegisterFluidSubtypesKubeEvent(registry));
		}

		if (remote != null) {
			for (var subtypes : remote.fluidData().dataComponentSubtypes()) {
				var fluids = Arrays.stream(subtypes.filter().getStacks()).map(FluidStack::getFluid).toArray(Fluid[]::new);

				registry.register((EntryComparator) DataComponentComparator.of(subtypes.components()), fluids);
			}
		}
	}
}