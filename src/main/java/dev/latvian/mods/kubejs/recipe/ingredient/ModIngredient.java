package dev.latvian.mods.kubejs.recipe.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.helpers.IngredientHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.Nullable;

public record ModIngredient(String mod) implements KubeJSIngredient {
	public static final MapCodec<ModIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("mod").forGetter(ModIngredient::mod)
	).apply(instance, ModIngredient::new));

	@Override
	public IngredientType<?> getType() {
		return IngredientHelper.MOD.get();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && stack.kjs$getMod().equals(mod);
	}
}
