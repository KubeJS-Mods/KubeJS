package dev.latvian.mods.kubejs.recipe.ingredient;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.helpers.IngredientHelper;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ModIngredient extends KubeJSIngredient {
	public static final Codec<ModIngredient> CODEC = Codec.STRING
		.fieldOf("mod")
		.codec()
		.xmap(ModIngredient::new, ingredient -> ingredient.mod);

	public final String mod;

	public ModIngredient(String mod) {
		super(IngredientHelper.MOD);
		this.mod = mod;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && stack.kjs$getMod().equals(mod);
	}
}
