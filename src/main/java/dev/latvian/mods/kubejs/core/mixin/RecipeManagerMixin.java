package dev.latvian.mods.kubejs.core.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;
import dev.latvian.mods.kubejs.net.KubeServerData;
import dev.latvian.mods.kubejs.net.SyncServerDataPayload;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.Cast;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@Mixin(value = RecipeManager.class, priority = 1100)
public abstract class RecipeManagerMixin extends ContextAwareReloadListener implements RecipeManagerKJS {
	@Unique
	private RecipeManager kjs$self() {
		return (RecipeManager) (Object) this;
	}

	@Shadow
	private RecipeMap recipes;

	@Final
	@Shadow
	private HolderLookup.Provider registries;

	@Unique
	private @Nullable ReloadableServerResourcesKJS kjs$resources;

	@Unique
	private @Nullable RecipesKubeEvent kjs$event;

	@Unique
	private @Nullable DynamicOps<JsonElement> kjs$cachedOps;

	@ModifyArg(
		method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimpleJsonResourceReloadListener;scanDirectory(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/FileToIdConverter;Lcom/mojang/serialization/DynamicOps;Lcom/mojang/serialization/Codec;Ljava/util/Map;)V"),
		index = 2
	)
	private DynamicOps<JsonElement> kjs$captureOps(DynamicOps<JsonElement> ops) {
		kjs$cachedOps = ops;
		return ops;
	}

	@Inject(
		method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;",
		at = @At("RETURN"),
		cancellable = true
	)
	private void kjs$interceptPrepare(ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfoReturnable<RecipeMap> cir) {
		if (kjs$resources == null) {
			return;
		}

		var manager = kjs$resources.kjs$getServerScriptManager();

		for (var pending : kjs$resources.kjs$getPostponedTags()) {
			pending.apply();
		}

		for (var pending : kjs$resources.kjs$getNewComponents()) {
			pending.apply();
		}

		for (var entry : manager.getRegistries().cachedRegistryTags.values()) {
			if (entry.registry() == null || entry.lookup() == null) {
				continue;
			}
			if (entry.registry() instanceof MappedRegistry<?> mappedRegistry) {
				mappedRegistry.bindTags(Cast.to(entry.lookup().bindingMap()));
			}
		}

		manager.recipeSchemaStorage.fireEvents(manager.getRegistries(), resourceManager);

		SpecialRecipeSerializerManager.INSTANCE.reset();
		ServerEvents.SPECIAL_RECIPES.post(ScriptType.SERVER, SpecialRecipeSerializerManager.INSTANCE);

		if (!ServerEvents.RECIPES.hasListeners()) {
			return;
		}

		ConsoleJS.SERVER.info("Processing recipes...");

		var ops = kjs$cachedOps != null ? kjs$cachedOps : new ConditionalOps<>(registries.createSerializationContext(JsonOps.INSTANCE), getContext());
		var jsonMap = new HashMap<Identifier, JsonElement>();

		var lister = FileToIdConverter.registry(Registries.RECIPE);
		for (var entry : lister.listMatchingResources(resourceManager).entrySet()) {
			var id = lister.fileToId(entry.getKey());
			try (var reader = entry.getValue().openAsReader()) {
				var json = JsonParser.parseReader(reader);
				if (json instanceof JsonObject obj) {
					jsonMap.put(id, obj);
				}
			} catch (Exception e) {
				ConsoleJS.SERVER.warn("Failed to read recipe JSON %s: %s".formatted(id, e.getMessage()));
			}
		}

		kjs$event = new RecipesKubeEvent(manager, resourceManager);
		kjs$event.post(this, jsonMap);

		var holders = new ArrayList<RecipeHolder<?>>();

		for (var entry : jsonMap.entrySet()) {
			var id = entry.getKey();

			if (entry.getValue() instanceof JsonObject jsonObj && jsonObj.has("type")) {
				Recipe.CODEC.parse(ops, jsonObj)
					.ifSuccess(recipe -> {
						var key = ResourceKey.create(Registries.RECIPE, id);
						holders.add(new RecipeHolder<>(key, recipe));
					})
					.ifError(err -> {
						if (kjs$event != null) {
							kjs$event.handleFailedRecipe(id, entry.getValue(), new RuntimeException(err.toString()));
						} else {
							ConsoleJS.SERVER.warn("Failed to parse modified recipe %s: %s".formatted(id, err.toString()));
						}
					});
			}
		}

		cir.setReturnValue(RecipeMap.create(holders));
	}

	@Inject(
		method = "apply(Lnet/minecraft/world/item/crafting/RecipeMap;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
		at = @At("TAIL")
	)
	private void kjs$applyTail(RecipeMap recipeMap, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
		if (kjs$event != null) {
			kjs$event.finishEvent();
		}

		kjs$event = null;
		kjs$cachedOps = null;

		if (!CommonProperties.get().serverOnly) {
			kjs$getServerScriptManager().serverData = new SyncServerDataPayload(KubeServerData.collect());
		}
	}

	@Override
	@NullUnmarked
	public ServerScriptManager kjs$getServerScriptManager() {
		return kjs$resources != null ? kjs$resources.kjs$getServerScriptManager() : null;
	}

	@Override
	public void kjs$setResources(ReloadableServerResourcesKJS resources) {
		kjs$resources = resources;
	}

	@Override
	public void kjs$replaceRecipes(RecipeMap recipeMap) {
		recipes = recipeMap;
		ConsoleJS.SERVER.info("Loaded " + recipeMap.values().size() + " recipes");
	}

	@Override
	public Collection<RecipeHolder<?>> kjs$getRecipes() {
		return recipes.values();
	}
}