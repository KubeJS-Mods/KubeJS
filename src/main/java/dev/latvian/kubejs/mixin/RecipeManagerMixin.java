package dev.latvian.kubejs.mixin;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJSCore;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * @author LatvianModder
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin
{
	@Inject(method = "apply", at = @At("RETURN"))
	private void customRecipes(Map<ResourceLocation, JsonObject> map, IResourceManager resourceManager, IProfiler profiler, CallbackInfo ci)
	{
		KubeJSCore.customRecipes((RecipeManager) (Object) this, map, resourceManager, profiler);
	}

	@Inject(method = "deserializeRecipe", at = @At("HEAD"), cancellable = true)
	private static void deserializeRecipe(ResourceLocation recipeId, JsonObject json, CallbackInfoReturnable<IRecipe<?>> ci)
	{
		if (ServerJS.instance != null && ServerJS.instance.betterRecipeErrorLogging)
		{
			ci.setReturnValue(KubeJSCore.customRecipeDeserializer(recipeId, json));
		}
	}
}