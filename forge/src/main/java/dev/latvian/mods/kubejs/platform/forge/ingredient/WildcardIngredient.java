package dev.latvian.mods.kubejs.platform.forge.ingredient;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.platform.forge.IngredientForgeHelper;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class WildcardIngredient extends KubeJSIngredient {
	public static WildcardIngredient INSTANCE = new WildcardIngredient();

	public static final Codec<WildcardIngredient> CODEC = Codec.unit(INSTANCE);

	private WildcardIngredient() {
		super(IngredientForgeHelper.WILDCARD);
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null;
	}

	@Override
	protected void dissolve() {
		if (this.itemStacks == null) {
			this.itemStacks = ItemStackJS.getList().toArray(new ItemStack[0]);
		}
	}
}
