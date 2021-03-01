package dev.latvian.kubejs.mixin.common;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.core.RecipeManagerKJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * @author LatvianModder
 */
@Mixin(value = RecipeManager.class, priority = 1100)
public abstract class RecipeManagerMixin implements RecipeManagerKJS {
	@Inject(method = "apply", at = @At("HEAD"), cancellable = true)
	private void customRecipesHead(Map<ResourceLocation, JsonObject> map, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
		customRecipesKJS(map);
		ci.cancel();
	}

	@Override
	@Accessor("recipes")
	public abstract void setRecipesKJS(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map);
}