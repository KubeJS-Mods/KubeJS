package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.MenuTypeKJS;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MenuType.class)
public abstract class MenuTypeMixin implements MenuTypeKJS {
}
