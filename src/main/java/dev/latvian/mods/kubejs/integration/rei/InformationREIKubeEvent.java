package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.plugin.client.BuiltinClientPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Arrays;

public class InformationREIKubeEvent implements KubeEvent {
	private final REIEntryWrappers entryWrappers;

	public InformationREIKubeEvent(REIEntryWrappers entryWrappers) {
		this.entryWrappers = entryWrappers;
	}

	public void addItem(Context cx, Ingredient stacks, Component title, Component[] description) {
		add(cx, VanillaEntryTypes.ITEM, stacks, title, description);
	}

	public void addFluid(Context cx, FluidStack[] stacks, Component title, Component[] description) {
		add(cx, VanillaEntryTypes.FLUID, stacks, title, description);
	}

	public void add(Context cx, ResourceLocation typeId, Object stacks, Component title, Component[] description) {
		add(cx, KubeJSREIPlugin.getTypeOrThrow(typeId), stacks, title, description);
	}

	@HideFromJS
	public <T> void add(Context cx, EntryType<T> type, Object stacks, Component title, Component[] description) {
		var w = entryWrappers.getWrapper(type);
		var list = w.entryList(cx, stacks);

		BuiltinClientPlugin.getInstance().registerInformation(
			EntryIngredient.of(list),
			title,
			components -> {
				components.addAll(Arrays.asList(description));
				return components;
			}
		);
	}
}