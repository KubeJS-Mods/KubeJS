package dev.latvian.mods.kubejs.integration.rei;

import dev.architectury.event.EventResult;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class KubeJSREIPlugin implements REIClientPlugin {
	private final Set<CategoryIdentifier<?>> categoriesRemoved = new HashSet<>();
	private final Map<CategoryIdentifier<?>, Collection<ResourceLocation>> recipesRemoved = new HashMap<>();

	private final REIEntryWrappers entryWrappers;

	public KubeJSREIPlugin() {
		entryWrappers = new REIEntryWrappers();
	}

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
		if (REIEvents.ADD.hasListeners()) {
			for (var wrapper : entryWrappers.getWrappers()) {
				if (REIEvents.ADD.hasListeners(wrapper.type().getId())) {
					REIEvents.ADD.post(new AddREIEventJS<>(registry, wrapper), wrapper.type().getId());
				}
			}
		}
	}

	@Override
	public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {
		if (REIEvents.HIDE.hasListeners()) {
			var registry = EntryRegistry.getInstance();
			var allEntries = registry.getEntryStacks().toArray(EntryStack[]::new);

			for (var wrapper : entryWrappers.getWrappers()) {
				if (REIEvents.HIDE.hasListeners(wrapper.type().getId())) {
					REIEvents.HIDE.post(new HideREIEventJS<>(registry, wrapper, rule, allEntries), wrapper.type().getId());
				}
			}
		}
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		if (REIEvents.INFORMATION.hasListeners()) {
			REIEvents.INFORMATION.post(new InformationREIEventJS(entryWrappers));
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

			if (REIEvents.REMOVE_CATEGORIES.hasListeners()) {
				REIEvents.REMOVE_CATEGORIES.post(new RemoveREICategoryEventJS(categoriesRemoved));
			}

			if (REIEvents.REMOVE_RECIPES.hasListeners()) {
				REIEvents.REMOVE_RECIPES.post(new RemoveREIRecipeEventJS(recipesRemoved));
			}
		}
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
		if (REIEvents.GROUP_ENTRIES.hasListeners()) {
			REIEvents.GROUP_ENTRIES.post(new GroupREIEntriesEventJS(entryWrappers, registry));
		}
	}

	public static EntryType<?> getTypeOrThrow(ResourceLocation typeId) {
		return Objects.requireNonNull(EntryTypeRegistry.getInstance().get(typeId), "Entry type '%s' not found!".formatted(typeId)).getType();
	}
}