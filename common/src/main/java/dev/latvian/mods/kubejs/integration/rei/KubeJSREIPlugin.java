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

import java.util.*;

/**
 * @author shedaniel
 */
public class KubeJSREIPlugin implements REIClientPlugin {
	private final Set<CategoryIdentifier<?>> categoriesRemoved = new HashSet<>();

	private static final Map<EntryType<?>, EntryWrapper> entryWrappers = new HashMap<>();

	public KubeJSREIPlugin() {
		entryWrappers.clear();
		entryWrappers.put(VanillaEntryTypes.ITEM, o -> EntryIngredients.ofIngredient(IngredientJS.of(o).createVanillaIngredient()));
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
		entryWrappers.forEach((type, wrapper) -> {
			var typeId = UtilsJS.stripIdForEvent(type.getId());
			var filteringRule = FilteringRuleTypeRegistry.getInstance().basic();

			new AddREIEventJS(registry, wrapper).post(ScriptType.CLIENT, REIIntegration.REI_ADD_EVENTS.formatted(typeId));

			if (type.getId().getNamespace().equals("minecraft")) {
				var shortId = UtilsJS.stripEventName(type.getId().getPath());
				new AddREIEventJS(registry, wrapper).post(ScriptType.CLIENT, REIIntegration.REI_ADD_EVENTS.formatted(shortId));
			}

			// legacy event ids with "plural s"
			if (type == VanillaEntryTypes.ITEM) {
				new AddREIEventJS(registry, wrapper).post(ScriptType.CLIENT, REIIntegration.REI_ADD_EVENTS.formatted("items"));
			} else if (type == VanillaEntryTypes.FLUID) {
				new AddREIEventJS(registry, wrapper).post(ScriptType.CLIENT, REIIntegration.REI_ADD_EVENTS.formatted("fluids"));
			}
		});
	}

	@Override
	public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {
		entryWrappers.forEach((type, wrapper) -> {
			var typeId = UtilsJS.stripIdForEvent(type.getId());
			var registry = EntryRegistry.getInstance();

			new HideREIEventJS<>(registry, rule, UtilsJS.cast(type), wrapper).post(ScriptType.CLIENT, REIIntegration.REI_HIDE_EVENTS.formatted(typeId));

			if (type.getId().getNamespace().equals("minecraft")) {
				var shortId = UtilsJS.stripEventName(type.getId().getPath());
				new HideREIEventJS<>(registry, rule, UtilsJS.cast(type), wrapper).post(ScriptType.CLIENT, REIIntegration.REI_HIDE_EVENTS.formatted(shortId));
			}

			// legacy event ids with "plural s"
			if (type == VanillaEntryTypes.ITEM) {
				new HideREIEventJS<>(registry, rule, VanillaEntryTypes.ITEM, wrapper).post(ScriptType.CLIENT, REIIntegration.REI_HIDE_EVENTS.formatted("items"));
			} else if (type == VanillaEntryTypes.FLUID) {
				new HideREIEventJS<>(registry, rule, VanillaEntryTypes.FLUID, wrapper).post(ScriptType.CLIENT, REIIntegration.REI_HIDE_EVENTS.formatted("fluids"));
			}
		});
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		new InformationREIEventJS().post(ScriptType.CLIENT, REIIntegration.REI_INFORMATION);
	}

	@Override
	public void registerCategories(CategoryRegistry registry) {
		registry.registerVisibilityPredicate(category -> categoriesRemoved.contains(category.getCategoryIdentifier())
				? EventResult.interruptFalse() : EventResult.pass());
	}

	@Override
	public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
		if (stage == ReloadStage.END) {
			categoriesRemoved.clear();
			new RemoveREICategoryEventJS(categoriesRemoved).post(ScriptType.CLIENT, REIIntegration.REI_REMOVE_CATEGORIES);
		}
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
		new REIGroupEntriesEventJS(registry).post(ScriptType.CLIENT, REIIntegration.REI_GROUP_ENTRIES);
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
			Collection<ResourceLocation> ids = CollectionUtils.mapToSet(ListJS.orSelf(o), UtilsJS::getMCID);
			return EntryRegistry.getInstance().getEntryStacks()
					.filter(stack -> stack.getType().equals(type))
					.filter(stack -> ids.contains(stack.getIdentifier()))
					.toList();
		};
	}
}