package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.DataPackRegistriesHelper;
import dev.latvian.kubejs.core.DataPackRegistriesKJS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.PackResources;

/**
 * @author LatvianModder
 */
@Mixin(ServerResources.class)
public abstract class DataPackRegistriesMixin implements DataPackRegistriesKJS
{
	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(CallbackInfo ci)
	{
		initKJS();
	}

	@ModifyArg(method = "func_240961_a_", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/resources/IReloadableResourceManager;reloadResourcesAndThen(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Ljava/util/concurrent/CompletableFuture;)Ljava/util/concurrent/CompletableFuture;"), index = 2)
	private static List<PackResources> resourcePackList(List<PackResources> list)
	{
		return DataPackRegistriesHelper.getResourcePackListKJS(list);
	}
}
