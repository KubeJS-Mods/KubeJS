package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ModifyRecipeResultKubeEvent implements KubeEvent {
	public final Player player;
	public final ModifyRecipeCraftingGrid grid;
	public ItemStack result;

	public ModifyRecipeResultKubeEvent(@Nullable Player player, ModifyRecipeCraftingGrid grid, ItemStack result) {
		this.player = player;
		this.grid = grid;
		this.result = result;
	}

	@Override
	public ItemStack defaultExitValue(Context cx) {
		return result;
	}

	@Override
	@HideFromJS
	public TypeInfo getExitValueType() {
		return ItemStackJS.TYPE_INFO;
	}
}