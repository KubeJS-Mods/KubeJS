package dev.latvian.mods.kubejs.platform.ingredient;

import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientPlatformHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.AbstractIngredient;

import java.util.stream.Stream;

public abstract class KubeJSIngredient extends AbstractIngredient implements IngredientKJS {
	public KubeJSIngredient() {
		super(Stream.empty());
		values = IngredientPlatformHelper.EMPTY_VALUES;
	}

	@Override
	public ItemStack[] getItems() {
		if (itemStacks == null) {
			dissolve();
		}

		return itemStacks;
	}

	@Override
	public void dissolve() {
		if (itemStacks == null) {
			itemStacks = kjs$getStacks().toArray();
		}
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	public abstract void write(FriendlyByteBuf buf);
}
