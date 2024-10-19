package dev.latvian.mods.kubejs.core.mixin;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;
import dev.latvian.mods.kubejs.net.KubeServerData;
import dev.latvian.mods.kubejs.net.SyncServerDataPayload;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.recipe.CompostableRecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.Cast;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = RecipeManager.class, priority = 1100)
public abstract class RecipeManagerMixin implements RecipeManagerKJS {
	@Shadow
	private Map<ResourceLocation, RecipeHolder<?>> byName;

	@Shadow
	private Multimap<RecipeType<?>, RecipeHolder<?>> byType;

	@Unique
	private ReloadableServerResourcesKJS kjs$resources;

	@Unique
	private RecipesKubeEvent kjs$event;

	@Inject(
		method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
		at = @At("HEAD")
	)
	private void customRecipesHead(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
		var manager = kjs$resources.kjs$getServerScriptManager();

		// FIXME: data maps!
		if (ServerEvents.COMPOSTABLE_RECIPES.hasListeners()) {
			ServerEvents.COMPOSTABLE_RECIPES.post(ScriptType.SERVER, new CompostableRecipesKubeEvent());
		}

		for (var entry : manager.getRegistries().cachedRegistryTags.values()) {
			if (entry.registry() == null || entry.lookup() == null) {
				continue;
			}

			entry.registry().bindTags(Cast.to(entry.lookup().bindingMap()));
		}

		manager.recipeSchemaStorage.fireEvents(manager.getRegistries(), resourceManager);

		SpecialRecipeSerializerManager.INSTANCE.reset();
		ServerEvents.SPECIAL_RECIPES.post(ScriptType.SERVER, SpecialRecipeSerializerManager.INSTANCE);

		if (ServerEvents.RECIPES.hasListeners()) {
			ConsoleJS.SERVER.info("Processing recipes...");
			kjs$event = new RecipesKubeEvent(manager, resourceManager);
			kjs$event.post(this, map);
		}
	}

	@Inject(
		method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
		at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V")
	)
	private void catchFailingRecipes(CallbackInfo ci, @Local Map.Entry<ResourceLocation, JsonElement> entry, @Local RuntimeException ex) {
		if (kjs$event != null) {
			kjs$event.handleFailedRecipe(entry.getKey(), entry.getValue(), ex);
		}
	}

	@Inject(
		method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
		at = @At(value = "TAIL")
	)
	@SuppressWarnings("removal")
	private void addServerData(CallbackInfo ci) {
		if (kjs$event != null) {
			// FIXME: please remove this soon! massive performance implications!
			var recipesByName = new HashMap<>(byName);

			KubeJSPlugins.forEachPlugin(p -> p.injectRuntimeRecipes(kjs$event, this, recipesByName));

			kjs$event.finishEvent();

			// make sure byType is also set correctly
			kjs$replaceRecipes(recipesByName);
		}

		kjs$event = null;

		kjs$getResources().kjs$getServerScriptManager().serverData = new SyncServerDataPayload(KubeServerData.collect());
	}

	@Override
	public void kjs$setResources(ReloadableServerResourcesKJS resources) {
		kjs$resources = resources;
	}

	@Override
	public ReloadableServerResourcesKJS kjs$getResources() {
		return kjs$resources;
	}

	@Override
	public Map<ResourceLocation, RecipeHolder<?>> kjs$getRecipeIdMap() {
		return byName;
	}

	@Override
	public void kjs$replaceRecipes(Map<ResourceLocation, RecipeHolder<?>> map) {
		byName = map;

		var recipesByType = ImmutableMultimap.<RecipeType<?>, RecipeHolder<?>>builder();

		for (var entry : map.entrySet()) {
			recipesByType.put(entry.getValue().value().getType(), entry.getValue());
		}

		byType = recipesByType.build();
		ConsoleJS.SERVER.info("Loaded " + byType.size() + " recipes");
	}
}