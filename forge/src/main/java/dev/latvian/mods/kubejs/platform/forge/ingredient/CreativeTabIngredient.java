package dev.latvian.mods.kubejs.platform.forge.ingredient;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.platform.forge.IngredientForgeHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CreativeTabIngredient extends KubeJSIngredient {
	public static final Codec<CreativeTabIngredient> CODEC = BuiltInRegistries.CREATIVE_MODE_TAB.byNameCodec()
		.fieldOf("tab")
		.codec()
		.xmap(CreativeTabIngredient::new, ingredient -> ingredient.tab);

	public final CreativeModeTab tab;

	public CreativeTabIngredient(CreativeModeTab tab) {
		super(IngredientForgeHelper.CREATIVE_TAB);
		this.tab = tab;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && tab.contains(stack);
	}
}
