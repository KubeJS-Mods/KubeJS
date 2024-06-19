package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagManager;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin implements ReloadableServerResourcesKJS {
	@Unique
	private ServerScriptManager kjs$serverScriptManager;

	@Shadow
	@Final
	private TagManager tagManager;

	@Shadow
	@Final
	private RecipeManager recipes;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(RegistryAccess.Frozen registryAccess, FeatureFlagSet featureFlagSet, Commands.CommandSelection commandSelection, int functionCompilationLevel, CallbackInfo ci) {
		kjs$serverScriptManager = ServerScriptManager.release();
		tagManager.kjs$setResources(this);
		recipes.kjs$setResources(this);
	}

	@Inject(method = "loadResources", at = @At("HEAD"))
	private static void injectKubeJSPacks(ResourceManager resourceManager, LayeredRegistryAccess<RegistryLayer> registries, FeatureFlagSet enabledFeatures, Commands.CommandSelection commandSelection, int functionCompilationLevel, Executor backgroundExecutor, Executor gameExecutor, CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir) {
		ServerScriptManager.capture(registries.compositeAccess());
	}

	@Override
	public ServerScriptManager kjs$getServerScriptManager() {
		return kjs$serverScriptManager;
	}

	@Override
	public TagManager kjs$getTagManager() {
		return tagManager;
	}
}
