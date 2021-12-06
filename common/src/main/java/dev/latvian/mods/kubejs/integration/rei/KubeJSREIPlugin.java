package dev.latvian.mods.kubejs.integration.rei;

import dev.architectury.event.EventResult;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * @author shedaniel
 */
public class KubeJSREIPlugin implements REIClientPlugin {
	private final Set<CategoryIdentifier<?>> categoriesRemoved = new HashSet<>();

	@Override
	public void registerEntries(EntryRegistry registry) {
		// TODO: i would like this system to be more extendable,
		//  such that other mod creators can registers serializers for their own
		//  entry types and we'd just handle firing the KubeJS events for them.
		Function<Object, Collection<EntryStack<?>>> itemSerializer = o -> EntryIngredients.ofItemStacks(
				CollectionUtils.map(IngredientJS.of(o).getStacks(), ItemStackJS::getItemStack));
		Function<Object, Collection<EntryStack<?>>> fluidSerializer = o -> EntryIngredients.of(FluidStackJS.of(o).getFluidStack());

		new HideREIEventJS<>(registry, VanillaEntryTypes.ITEM, itemSerializer).post(ScriptType.CLIENT, REIIntegration.REI_HIDE_ITEMS);
		new HideREIEventJS<>(registry, VanillaEntryTypes.FLUID, fluidSerializer).post(ScriptType.CLIENT, REIIntegration.REI_HIDE_FLUIDS);
		new AddREIEventJS(registry, itemSerializer).post(ScriptType.CLIENT, REIIntegration.REI_ADD_ITEMS);
		new AddREIEventJS(registry, fluidSerializer).post(ScriptType.CLIENT, REIIntegration.REI_ADD_FLUIDS);
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		// TODO: can we make this fire for more entry types than just items?
		new InformationREIEventJS().post(ScriptType.CLIENT, REIIntegration.REI_INFORMATION);
	}

	@Override
	public void registerCategories(CategoryRegistry registry) {
		registry.registerVisibilityPredicate(category -> {
			return categoriesRemoved.contains(category.getCategoryIdentifier()) ? EventResult.interruptFalse() : EventResult.pass();
		});
	}

	@Override
	public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
		if (stage == ReloadStage.END) {
			categoriesRemoved.clear();
			new RemoveREICategoryEventJS(categoriesRemoved).post(ScriptType.CLIENT, REIIntegration.REI_REMOVE_CATEGORIES);
		}
	}
}