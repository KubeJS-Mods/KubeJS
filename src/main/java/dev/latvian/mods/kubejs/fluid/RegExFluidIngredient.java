package dev.latvian.mods.kubejs.fluid;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class RegExFluidIngredient extends FluidIngredient {
	public static final MapCodec<RegExFluidIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		RegExpKJS.CODEC.fieldOf("regex").forGetter(i -> i.pattern)
	).apply(instance, RegExFluidIngredient::new));

	public static final StreamCodec<ByteBuf, RegExFluidIngredient> STREAM_CODEC = RegExpKJS.STREAM_CODEC.map(RegExFluidIngredient::new, i -> i.pattern);

	public final Pattern pattern;
	public final String patternString;

	public RegExFluidIngredient(Pattern pattern) {
		this.pattern = pattern;
		this.patternString = RegExpKJS.toRegExpString(pattern);
	}

	@Override
	public FluidIngredientType<?> getType() {
		return KubeJSFluidIngredients.REGEX.get();
	}

	@Override
	public boolean test(FluidStack fs) {
		return pattern.matcher(fs.getFluid().kjs$getId()).find();
	}

	@Override
	protected Stream<FluidStack> generateStacks() {
		return BuiltInRegistries.FLUID.stream().filter(fluid -> pattern.matcher(fluid.kjs$getId()).find()).map(fluid -> new FluidStack(fluid, FluidType.BUCKET_VOLUME));
	}

	@Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public int hashCode() {
		return patternString.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof RegExFluidIngredient r && patternString.equals(r.patternString);
	}
}
