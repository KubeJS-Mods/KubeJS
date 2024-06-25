package dev.latvian.mods.kubejs.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;

import java.util.stream.Stream;

public class NamespaceFluidIngredient extends FluidIngredient {
	public static final MapCodec<NamespaceFluidIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("namespace").forGetter(i -> i.namespace)
	).apply(instance, NamespaceFluidIngredient::new));

	public static final StreamCodec<ByteBuf, NamespaceFluidIngredient> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(NamespaceFluidIngredient::new, i -> i.namespace);

	public final String namespace;

	public NamespaceFluidIngredient(String namespace) {
		this.namespace = namespace;
	}

	@Override
	public FluidIngredientType<?> getType() {
		return KubeJSFluidIngredients.NAMESPACE.get();
	}

	@Override
	public boolean test(FluidStack fs) {
		return fs.getFluid().kjs$getMod().equals(namespace);
	}

	@Override
	protected Stream<FluidStack> generateStacks() {
		return BuiltInRegistries.FLUID.stream().filter(fluid -> fluid.kjs$getMod().equals(namespace)).map(fluid -> new FluidStack(fluid, FluidType.BUCKET_VOLUME));
	}

	@Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public int hashCode() {
		return namespace.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof NamespaceFluidIngredient r && namespace.equals(r.namespace);
	}
}
