package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.ImageButtonKJS;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(ImageButton.class)
public abstract class ImageButtonMixin implements ImageButtonKJS {
	@Override
	@Accessor("resourceLocation")
	public abstract ResourceLocation kjs$getButtonTexture();
}