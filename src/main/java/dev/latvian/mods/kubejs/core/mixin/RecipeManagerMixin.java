package dev.latvian.mods.kubejs.core.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import dev.latvian.mods.kubejs.util.Cast;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@Mixin(value = RecipeManager.class, priority = 1100)
public abstract class RecipeManagerMixin implements RecipeManagerKJS {
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
	private ReloadableServerResourcesKJS kjs$resources;

	@Unique
	private RecipesKubeEvent kjs$event;

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

		// Apply postponed tags first so that tag-based ingredients can be resolved during encoding
		for (var pending : kjs$resources.kjs$getPostponedTags()) {
			pending.apply();
		}

		// Apply pending components so that items have their data components available during recipe parsing
		for (var pending : kjs$resources.kjs$getNewComponents()) {
			pending.apply();
		}

		// Bind registry tags
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

		var ops = new ConditionalOps<>(
			manager.getRegistries().access().createSerializationContext(JsonOps.INSTANCE),
			kjs$self().kjs$getContext()
		);
		var jsonMap = new HashMap<Identifier, JsonElement>();

		for (var holder : cir.getReturnValue().values()) {
			Recipe.CODEC.encodeStart(ops, holder.value())
				.ifSuccess(encoded -> {
					if (encoded instanceof JsonObject obj) {
						jsonMap.put(holder.id().identifier(), obj);
					}
				})
				.ifError(err -> ConsoleJS.SERVER.warn("Failed to encode recipe %s for KubeJS: %s".formatted(holder.id().identifier(), err.toString())));
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

		if (!CommonProperties.get().serverOnly) {
			kjs$getResources().kjs$getServerScriptManager().serverData = new SyncServerDataPayload(KubeServerData.collect());
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