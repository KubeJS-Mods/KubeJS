package dev.latvian.mods.kubejs.core.mixin;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SimpleJsonResourceReloadListener.class)
public class SimpleJsonResourceReloadListenerMixin {
	@ModifyExpressionValue(method = "scanDirectoryWithModifier", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/DataResult;ifError(Ljava/util/function/Consumer;)Lcom/mojang/serialization/DataResult;"))
	private static <T> DataResult<T> onScanDirectoryWithModifier(DataResult<T> result, @Local(name = "id") Identifier id, @Local(name = "json") JsonElement json) {
		if (result instanceof DataResult.Error<T> error && RecipesKubeEvent.INSTANCE.isBound()) {
			RecipesKubeEvent.INSTANCE.get().handleFailedRecipe(id, json, new IllegalStateException(error.message()));
		}
		return result;
	}
}
