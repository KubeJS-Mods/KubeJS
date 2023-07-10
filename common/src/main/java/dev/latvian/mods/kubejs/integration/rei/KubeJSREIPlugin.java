package dev.latvian.mods.kubejs.integration.rei;

import dev.architectury.event.EventResult;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import me.shedaniel.rei.api.client.entry.filtering.FilteringRuleTypeRegistry;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

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

	private static final Map<EntryType<?>, EntryWrapper> entryWrappers = new HashMap<>();

	public KubeJSREIPlugin() {
		entryWrappers.clear();
		entryWrappers.put(VanillaEntryTypes.ITEM, o -> EntryIngredients.ofItemStacks(o instanceof ItemStack is ? List.of(is) : IngredientJS.of(o).kjs$getDisplayStacks().toList()));
		entryWrappers.put(VanillaEntryTypes.FLUID, o -> EntryIngredients.of(FluidStackJS.of(o).getFluidStack()));
		KubeJSAddREIWrapperEvent.EVENT.invoker().accept(entryWrappers::put);
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
			entryWrappers.forEach((type, wrapper) -> REIEvents.ADD.post(new AddREIEventJS(registry, wrapper), type.getId()));
		}
	}

	@Override
	public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {
		if (REIEvents.HIDE.hasListeners()) {
			entryWrappers.forEach((type, wrapper) -> {
				var filter = FilteringRuleTypeRegistry.getInstance().basic();
				var registry = EntryRegistry.getInstance();

				REIEvents.HIDE.post(new HideREIEventJS<>(registry, filter, UtilsJS.cast(type), wrapper), type.getId());
			});
		}
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		if (REIEvents.INFORMATION.hasListeners()) {
			REIEvents.INFORMATION.post(new InformationREIEventJS());
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
			REIEvents.GROUP_ENTRIES.post(new GroupREIEntriesEventJS(registry));
		}
	}

	public static EntryType<?> getTypeOrThrow(ResourceLocation typeId) {
		return Objects.requireNonNull(EntryTypeRegistry.getInstance().get(typeId), "Entry type '%s' not found!".formatted(typeId)).getType();
	}

	public static EntryWrapper getWrapperOrFallback(EntryType<?> type) {
		var wrapper = entryWrappers.get(type);
		if (wrapper != null) {
			return wrapper;
		}

		ConsoleJS.CLIENT.warn("No wrapper found for entry type '%s', trying to fall back to id-based wrapper!".formatted(type.getId()));
		return o -> {
			Collection<ResourceLocation> ids = CollectionUtils.mapToSet(ListJS.orSelf(o), o1 -> UtilsJS.getMCID(ScriptType.CLIENT.manager.get().context, o1));
			return EntryRegistry.getInstance().getEntryStacks()
					.filter(stack -> stack.getType().equals(type))
					.filter(stack -> ids.contains(stack.getIdentifier()))
					.toList();
		};
	}
}