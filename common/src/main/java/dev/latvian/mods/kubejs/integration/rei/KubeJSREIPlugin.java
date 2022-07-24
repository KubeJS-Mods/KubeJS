package dev.latvian.mods.kubejs.integration.rei;

import dev.architectury.event.EventResult;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author shedaniel
 */
public class KubeJSREIPlugin implements REIClientPlugin {
	private final Set<CategoryIdentifier<?>> categoriesRemoved = new HashSet<>();

	private static final Map<EntryType<?>, EntryWrapper> entryWrappers = new HashMap<>();

	public KubeJSREIPlugin() {
		entryWrappers.clear();
		entryWrappers.put(VanillaEntryTypes.ITEM, o -> EntryIngredients.ofItemStacks(IngredientJS.of(o).getStacks().toList()));
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
			REIKubeJSEvents.HIDE.post(type.getId(), new HideREIEventJS<>(registry, UtilsJS.cast(type), wrapper));
			REIKubeJSEvents.ADD.post(type.getId(), new AddREIEventJS(registry, wrapper));
		});
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		REIKubeJSEvents.INFORMATION.post(new InformationREIEventJS());
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
			REIKubeJSEvents.REMOVE_CATEGORIES.post(new RemoveREICategoryEventJS(categoriesRemoved));
		}
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