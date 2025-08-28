package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.RecipeHolderKJS;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RecipeHolder.class)
public class RecipeHolderMixin implements RecipeHolderKJS {
	@Unique
	private ResourceKey<RecipeSerializer<?>> kjs$serializerKey;

	@Override
	public ResourceKey<RecipeSerializer<?>> kjs$getTypeKey() {
		if (kjs$serializerKey == null) {
			kjs$serializerKey = BuiltInRegistries.RECIPE_SERIALIZER.getResourceKey(kjs$getSerializer()).orElseThrow();
		}

		return kjs$serializerKey;
	}
}
