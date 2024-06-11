package dev.latvian.mods.kubejs.core.mixin;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;
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

import java.util.Map;

@Mixin(value = RecipeManager.class, priority = 1100)
public abstract class RecipeManagerMixin implements RecipeManagerKJS {
	@Shadow
	private Map<ResourceLocation, RecipeHolder<?>> byName;

	@Shadow
	private Multimap<RecipeType<?>, RecipeHolder<?>> byType;

	@Unique
	private ReloadableServerResourcesKJS kjs$resources;

	@Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"), cancellable = true)
	private void customRecipesHead(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
		if (kjs$resources.kjs$getServerScriptManager().recipes(this, resourceManager, map)) {
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
	}
}