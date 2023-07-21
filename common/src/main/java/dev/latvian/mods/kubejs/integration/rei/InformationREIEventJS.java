package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.plugin.client.BuiltinClientPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;

public class InformationREIEventJS extends EventJS {
	public void addItem(Ingredient stacks, Component title, Component[] description) {
		add(VanillaEntryTypes.ITEM, stacks, title, description);
	}

	public void addFluid(FluidStackJS stacks, Component title, Component[] description) {
		add(VanillaEntryTypes.FLUID, stacks, title, description);
	}

	public void add(ResourceLocation typeId, Object stacks, Component title, Component[] description) {
		add(KubeJSREIPlugin.getTypeOrThrow(typeId), stacks, title, description);
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