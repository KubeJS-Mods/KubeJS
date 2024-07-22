package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.core.ContainerKJS;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Container.class)
public interface ContainerMixin extends ContainerKJS {
	@ModifyConstant(method = "getMaxStackSize()I", constant = @Constant(intValue = 99))
	private int kjs$maxSlotSize(int original) {
		return CommonProperties.get().getMaxSlotSize(original);
	}
}
