package dev.latvian.mods.kubejs.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public record RegExIngredient(Pattern pattern) implements KubeJSIngredient {
	public static final MapCodec<RegExIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		RegExpKJS.CODEC.fieldOf("pattern").forGetter(RegExIngredient::pattern)
	).apply(instance, RegExIngredient::new));

	@Override
	public IngredientType<?> getType() {
		return KubeJSIngredients.REGEX.get();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && pattern.matcher(stack.kjs$getId()).find();
	}
}
