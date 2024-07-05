package dev.latvian.mods.kubejs.core.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(WorldLoader.PackConfig.class)
public abstract class WorldLoaderPackConfigMixin {
	@ModifyExpressionValue(method = "createResourceManager", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;openAllSelected()Ljava/util/List;"))
	private List<PackResources> kjs$getPackResources(List<PackResources> original) {
		return ServerScriptManager.createPackResources(original);
	}
}
