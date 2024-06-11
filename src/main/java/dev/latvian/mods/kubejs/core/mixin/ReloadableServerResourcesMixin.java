package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;
import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
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
		kjs$serverScriptManager = new ServerScriptManager((ReloadableServerResources) (Object) this, registryAccess);
		UtilsJS.staticRegistries = kjs$serverScriptManager.registries;
		UtilsJS.staticNbtRegistryOps = kjs$serverScriptManager.nbtRegistryOps;
		UtilsJS.staticJsonRegistryOps = kjs$serverScriptManager.jsonRegistryOps;
		tagManager.kjs$setResources(this);
		recipes.kjs$setResources(this);
		kjs$serverScriptManager.reload();
	}

	@Inject(method = "updateRegistryTags(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/tags/TagManager$LoadResult;)V", at = @At("RETURN"))
	private static void updateRegistryTags(RegistryAccess registryAccess, TagManager.LoadResult<?> result, CallbackInfo ci) {
		TagContext.INSTANCE.setValue(TagContext.usingRegistry(registryAccess));
	}

	/* FIXME
	@Inject(method = "loadResources", at = @At("HEAD"))
	private static void injectKubeJSPacks(
		ResourceManager manager,
		LayeredRegistryAccess<RegistryLayer> registryAccess,
		FeatureFlagSet featureFlagSet,
		Commands.CommandSelection commandSelection,
		int functionCompilationLevel,
		Executor loadExecutor,
		Executor applyExecutor,
		CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir
	) {
		ServerScriptManager.instance.reload(manager);
	}
	 */

	@Override
	public ServerScriptManager kjs$getServerScriptManager() {
		return kjs$serverScriptManager;
	}

	@Override
	public TagManager kjs$getTagManager() {
		return tagManager;
	}
}
