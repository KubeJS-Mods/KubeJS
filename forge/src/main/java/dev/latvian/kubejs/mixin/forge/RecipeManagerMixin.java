package dev.latvian.kubejs.mixin.forge;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.core.RecipeManagerKJS;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * @author LatvianModder
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin implements RecipeManagerKJS
{
	@Inject(method = "apply", at = @At("HEAD"), cancellable = true)
	private void customRecipesHead(Map<ResourceLocation, JsonObject> map, IResourceManager resourceManager, IProfiler profiler, CallbackInfo ci)
	{
		customRecipesKJS(map);
		ci.cancel();
	}

	@Override
	@Accessor("recipes")
	public abstract void setRecipesKJS(Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> map);
}