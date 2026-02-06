package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.permissions.PermissionSet;
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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin implements ReloadableServerResourcesKJS {
	@Unique
	private ServerScriptManager kjs$serverScriptManager;

	@Shadow
	@Final
	private RecipeManager recipes;

	@Shadow
	@Final
	private HolderLookup.Provider registryLookup;

	@Shadow
	@Final
	private List<Registry.PendingTags<?>> postponedTags;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void kjs$init(
		LayeredRegistryAccess<RegistryLayer> fullLayers,
		HolderLookup.Provider loadingContext,
		FeatureFlagSet enabledFeatures,
		Commands.CommandSelection commandSelection,
		List<Registry.PendingTags<?>> postponedTags,
		PermissionSet functionCompilationPermissions,
		CallbackInfo ci
	) {
		kjs$serverScriptManager = ServerScriptManager.release();
		recipes.kjs$setResources(this);
	}

	@Inject(method = "loadResources", at = @At("HEAD"))
	private static void kjs$injectKubeJSPacks(
		ResourceManager resourceManager,
		LayeredRegistryAccess<RegistryLayer> contextLayers,
		List<Registry.PendingTags<?>> updatedContextTags,
		FeatureFlagSet enabledFeatures,
		Commands.CommandSelection commandSelection,
		PermissionSet functionCompilationPermissions,
		Executor backgroundExecutor,
		Executor mainThreadExecutor,
		CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir
	) {
		RegistryAccessContainer.current = new RegistryAccessContainer(contextLayers.compositeAccess());

		if (mainThreadExecutor instanceof MinecraftServer s && s.getServerResources() != null) {
			var mgr = s.getServerResources().managers().kjs$getServerScriptManager();
			if (mgr != null) {
				mgr.reloadAndCapture();
			}
		}
	}

	@Override
	public ServerScriptManager kjs$getServerScriptManager() {
		return kjs$serverScriptManager;
	}

	@Override
	public HolderLookup.Provider kjs$getRegistryLookup() {
		return registryLookup;
	}

	@Override
	public List<Registry.PendingTags<?>> kjs$getPostponedTags() {
		return postponedTags;
	}
}
