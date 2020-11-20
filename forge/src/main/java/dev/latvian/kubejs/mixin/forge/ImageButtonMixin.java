package dev.latvian.kubejs.mixin.forge;

import dev.latvian.kubejs.core.ImageButtonKJS;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(ImageButton.class)
public abstract class ImageButtonMixin implements ImageButtonKJS
{
	@Override
	@Accessor("resourceLocation")
	public abstract ResourceLocation getButtonTextureKJS();
}