package dev.latvian.mods.kubejs.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record ItemTintFunctionWrapper(int index) implements ItemTintSource {
	public static final MapCodec<ItemTintFunctionWrapper> CODEC = RecordCodecBuilder.mapCodec(i ->
		i.group(
			Codec.INT.optionalFieldOf("index", 0).forGetter(ItemTintFunctionWrapper::index)
		).apply(i, ItemTintFunctionWrapper::new)
	);

	@Override
	public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
		var item = stack.getItem();
		var builder = item.kjs$getItemBuilder();

		if (builder != null && builder.tint != null) {
			var c = builder.tint.getColor(stack, index);
			return c == null ? 0xFFFFFFFF : c.kjs$getARGB();
		}

		return 0xFFFFFFFF;
	}

	@Override
	public MapCodec<? extends ItemTintSource> type() {
		return CODEC;
	}
}
