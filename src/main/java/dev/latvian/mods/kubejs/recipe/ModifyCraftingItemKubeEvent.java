package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeInput;

public class ModifyCraftingItemKubeEvent implements KubeEvent {
	public final RecipeInput grid;
	public final int width;
	public final int height;
	public ItemStack item;
	public final int index;

	public ModifyCraftingItemKubeEvent(RecipeInput grid, int width, int height, ItemStack item, int index) {
		this.grid = grid;
		this.width = width;
		this.height = height;
		this.item = item;
		this.index = index;
	}

	public ModifyCraftingItemKubeEvent(CraftingInput grid, ItemStack item, int index) {
		this(grid, grid.width(), grid.height(), item, index);
	}

	@Override
	public ItemStack defaultExitValue(Context cx) {
		return item;
	}

	@Override
	@HideFromJS
	public TypeInfo getExitValueType() {
		return ItemWrapper.TYPE_INFO;
	}
}