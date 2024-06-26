package dev.latvian.mods.kubejs.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public record RegExIngredient(Pattern pattern, String patternString) implements KubeJSIngredient {
	public static final MapCodec<RegExIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		RegExpKJS.CODEC.fieldOf("pattern").forGetter(RegExIngredient::pattern)
	).apply(instance, RegExIngredient::new));

	public static final StreamCodec<ByteBuf, RegExIngredient> STREAM_CODEC = RegExpKJS.STREAM_CODEC.map(RegExIngredient::new, RegExIngredient::pattern);

	public RegExIngredient(Pattern pattern) {
		this(pattern, RegExpKJS.toRegExpString(pattern));
	}

	@Override
	public IngredientType<?> getType() {
		return KubeJSIngredients.REGEX.get();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && pattern.matcher(stack.kjs$getId()).find();
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof RegExIngredient i && patternString.equals(i.patternString);
	}

	@Override
	public int hashCode() {
		return patternString.hashCode();
	}

	@Override
	public String toString() {
		return "KubeJSItemRegExIngredient[" + patternString + "]";
	}
}
