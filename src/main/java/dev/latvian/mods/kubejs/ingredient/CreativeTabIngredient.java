package dev.latvian.mods.kubejs.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.Nullable;

public record CreativeTabIngredient(CreativeModeTab tab) implements KubeJSIngredient {
	public static final MapCodec<CreativeTabIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BuiltInRegistries.CREATIVE_MODE_TAB.byNameCodec().fieldOf("tab").forGetter(CreativeTabIngredient::tab)
	).apply(instance, CreativeTabIngredient::new));

	@Override
	public IngredientType<?> getType() {
		return KubeJSIngredients.CREATIVE_TAB.get();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && tab.contains(stack);
	}
}
