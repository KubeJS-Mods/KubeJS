package dev.latvian.mods.kubejs.core.mixin;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;
import dev.latvian.mods.kubejs.recipe.CompostableRecipesKubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.Cast;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = RecipeManager.class, priority = 1100)
public abstract class RecipeManagerMixin implements RecipeManagerKJS {
	@Unique
	private ReloadableServerResourcesKJS kjs$resources;

	@Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"), cancellable = true)
	private void customRecipesHead(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
		if (ServerEvents.COMPOSTABLE_RECIPES.hasListeners()) {
			ServerEvents.COMPOSTABLE_RECIPES.post(ScriptType.SERVER, new CompostableRecipesKubeEvent());
		}

		var manager = kjs$resources.kjs$getServerScriptManager();
		manager.recipeSchemaStorage.fireEvents(resourceManager);

		if (manager.recipesEvent != null) {
			manager.recipesEvent.post(Cast.to(this), map);
			manager.recipesEvent = null;
			ci.cancel();
		}
	}

	@Override
	public void kjs$setResources(ReloadableServerResourcesKJS resources) {
		kjs$resources = resources;
	}

	@Override
	public ReloadableServerResourcesKJS kjs$getResources() {
		return kjs$resources;
	}
}