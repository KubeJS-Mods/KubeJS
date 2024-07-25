package dev.latvian.mods.kubejs.client.editor;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class SelectItemStackScreen extends Screen {
	public final EditorCallback<ItemStack> callback;

	protected SelectItemStackScreen(EditorCallback<ItemStack> callback) {
		super(Component.literal("Select Item"));
		this.callback = callback;
	}

	@Override
	protected void init() {
		super.init();
	}
}
