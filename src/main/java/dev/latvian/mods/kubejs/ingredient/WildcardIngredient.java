package dev.latvian.mods.kubejs.ingredient;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.Nullable;

public class WildcardIngredient implements KubeJSIngredient {
	public static WildcardIngredient INSTANCE = new WildcardIngredient();

	public static final MapCodec<WildcardIngredient> CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<ByteBuf, WildcardIngredient> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	private WildcardIngredient() {
	}

	@Override
	public IngredientType<?> getType() {
		return KubeJSIngredients.WILDCARD.get();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && !stack.isEmpty();
	}
}
