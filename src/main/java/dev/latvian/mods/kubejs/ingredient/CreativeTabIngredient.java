package dev.latvian.mods.kubejs.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.Nullable;

public record CreativeTabIngredient(CreativeModeTab tab) implements KubeJSIngredient {
	public static final MapCodec<CreativeTabIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BuiltInRegistries.CREATIVE_MODE_TAB.byNameCodec().fieldOf("tab").forGetter(CreativeTabIngredient::tab)
	).apply(instance, CreativeTabIngredient::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, CreativeTabIngredient> STREAM_CODEC = ByteBufCodecs.registry(Registries.CREATIVE_MODE_TAB).map(CreativeTabIngredient::new, CreativeTabIngredient::tab);

	@Override
	public IngredientType<?> getType() {
		return KubeJSIngredients.CREATIVE_TAB.get();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && tab.contains(stack);
	}
}
