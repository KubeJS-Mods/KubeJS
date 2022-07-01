package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.plugin.client.BuiltinClientPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author shedaniel
 */
public class InformationREIEventJS extends EventJS {

	@Deprecated(forRemoval = true)
	public void add(Object stacks, Component title, Component[] description) {
		ConsoleJS.CLIENT.warn("add(stack, title, desc) is deprecated and will be removed in the future.");
		ConsoleJS.CLIENT.warn("To add a description to items or fluids, use addItem(…) or addFluid(…) respectively, or add(type, stacks, title, desc) for other entry types.");
		addItem(IngredientJS.of(stacks), title, description);
	}

	public void addItem(IngredientJS stacks, Component title, Component[] description) {
		add(VanillaEntryTypes.ITEM, stacks, title, description);
	}

	public void addFluid(FluidStackJS stacks, Component title, Component[] description) {
		add(VanillaEntryTypes.FLUID, stacks, title, description);
	}

	public void add(ResourceLocation typeId, Object stacks, Component title, Component[] description) {
		add(Objects.requireNonNull(EntryTypeRegistry.getInstance().get(typeId), "Entry type '%s' not found!".formatted(typeId)).getType(), stacks, title, description);
	}

	@HideFromJS
	public void add(EntryType<?> type, Object stacks, Component title, Component[] description) {
		BuiltinClientPlugin.getInstance().registerInformation(
				EntryIngredient.of(KubeJSREIPlugin.getWrapperOrFallback(type).wrap(stacks)),
				title,
				components -> {
					components.addAll(Arrays.asList(description));
					return components;
				}
		);
	}
}