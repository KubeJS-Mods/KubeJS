package dev.latvian.mods.kubejs.mixin.fabric;

import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {

	@Shadow
	@Final
	private static Map<String, ResourceLocation> ARMOR_LOCATION_CACHE;

	@Inject(method = "getArmorLocation", at = @At("HEAD"), cancellable = true)
	private void tryOverwriteArmorLocation(ArmorItem armorItem, boolean useSecondLayer, String suffix, CallbackInfoReturnable<ResourceLocation> cir) {
		// If our armor item has a namespace, we will add this to the texture path
		String materialName = armorItem.getMaterial().getName();
		int separatorIndex = materialName.indexOf(':');
		if (separatorIndex != -1) {
			String namespace = materialName.substring(0, separatorIndex);
			String path = materialName.substring(separatorIndex + 1);
			ResourceLocation texture = new ResourceLocation(namespace, "textures/models/armor/" + path + "_layer_" + (useSecondLayer ? 2 : 1) + (suffix == null ? "" : "_" + suffix) + ".png");
			if(!ARMOR_LOCATION_CACHE.containsKey(texture.toString())) {
				ARMOR_LOCATION_CACHE.put(texture.toString(), texture);
			}
			cir.setReturnValue(texture);
		}
	}
}
