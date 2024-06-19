package dev.latvian.mods.kubejs.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.Nullable;

public record NamespaceIngredient(String mod) implements KubeJSIngredient {
	public static final MapCodec<NamespaceIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("namespace").forGetter(NamespaceIngredient::mod)
	).apply(instance, NamespaceIngredient::new));

	@Override
	public IngredientType<?> getType() {
		return KubeJSIngredients.NAMESPACE.get();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && stack.kjs$getMod().equals(mod);
	}
}
