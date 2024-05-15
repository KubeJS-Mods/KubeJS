package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BasicItemJS extends Item {
	public static class Builder extends ItemBuilder {
		public Builder(ResourceLocation i) {
			super(i);
		}

		@Override
		public Item createObject() {
			return new BasicItemJS(this);
		}
	}

	private final ItemBuilder itemBuilder;

	public BasicItemJS(ItemBuilder p) {
		super(p.createItemProperties());
		this.itemBuilder = p;
	}

	@Override
	public ItemBuilder kjs$getItemBuilder() {
		return itemBuilder;
	}

	@Override
	public Component getName(ItemStack itemStack) {
		if (itemBuilder.displayName != null && itemBuilder.formattedDisplayName) {
			return itemBuilder.displayName;
		}

		return super.getName(itemStack);
	}
}