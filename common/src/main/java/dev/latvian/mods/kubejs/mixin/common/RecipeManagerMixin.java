package dev.latvian.mods.kubejs.mixin.common;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.recipe.CompostablesRecipeEventJS;
import dev.latvian.mods.kubejs.recipe.RecipeEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * @author LatvianModder
 */
@Mixin(value = RecipeManager.class, priority = 1100)
public abstract class RecipeManagerMixin {
	@Inject(method = "apply*", at = @At("HEAD"), cancellable = true)
	private void customRecipesHead(Map<ResourceLocation, JsonObject> map, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
		if (RecipeEventJS.instance != null) {
			RecipeEventJS.instance.post(UtilsJS.cast(this), map);
			new CompostablesRecipeEventJS().post(ScriptType.SERVER, KubeJSEvents.RECIPES_COMPOSTABLES);
			RecipeEventJS.instance = null;
		}
		ci.cancel();
	}
}