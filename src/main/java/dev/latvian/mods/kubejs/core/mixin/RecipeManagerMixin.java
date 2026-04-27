package dev.latvian.mods.kubejs.core.mixin;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;
import dev.latvian.mods.kubejs.net.KubeServerData;
import dev.latvian.mods.kubejs.net.SyncServerDataPayload;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.Cast;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

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

	@WrapOperation(
		method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/packs/resources/SimpleJsonResourceReloadListener;scanDirectoryWithModifier(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/FileToIdConverter;Lcom/mojang/serialization/DynamicOps;Lcom/mojang/serialization/Codec;Ljava/util/Map;Ljava/util/function/Consumer;)V"
		)
	)
	private void injectEventToPost(
		ResourceManager manager,
		FileToIdConverter lister,
		DynamicOps<JsonElement> ops,
		Codec<Recipe<?>> codec,
		Map<Identifier, Recipe<?>> result,
		Consumer<Map<Identifier, JsonElement>> jsonConsumer,
		Operation<Void> original
	) {
		if (kjs$resources == null) {
			return;
		}

		var ssm = Objects.requireNonNull(kjs$resources.kjs$getServerScriptManager());

		// TODO: i would like to be able to live without these two calls
		for (var pending : kjs$resources.kjs$getPostponedTags()) {
			pending.apply();
		}

		for (var pending : kjs$resources.kjs$getNewComponents()) {
			pending.apply();
		}

		for (var entry : ssm.getRegistries().cachedRegistryTags.values()) {
			if (entry.registry() instanceof MappedRegistry<?> mappedRegistry) {
				mappedRegistry.bindTags(Cast.to(entry.lookup().bindingMap()));
			}
		}

		ssm.recipeSchemaStorage.fireEvents(ssm.getRegistries(), manager);

		SpecialRecipeSerializerManager.INSTANCE.reset();
		ServerEvents.SPECIAL_RECIPES.post(ScriptType.SERVER, SpecialRecipeSerializerManager.INSTANCE);

		// this will end up calling the NF event which will post the kube event
		ScopedValue.where(RecipesKubeEvent.INSTANCE, new RecipesKubeEvent(ssm)).run(() -> {
			ScriptType.SERVER.console.info("Processing recipes...");
			original.call(manager, lister, ops, codec, result, jsonConsumer);
		});
	}

	@Inject(
		method = "apply(Lnet/minecraft/world/item/crafting/RecipeMap;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
		at = @At("TAIL")
	)
	private void kjs$applyTail(RecipeMap recipeMap, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
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
		ScriptType.SERVER.console.info("Loaded " + recipeMap.values().size() + " recipes");
	}

	@Override
	public Collection<RecipeHolder<?>> kjs$getRecipes() {
		return recipes.values();
	}
}