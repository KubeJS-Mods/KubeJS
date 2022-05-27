package dev.latvian.mods.kubejs.mixin.forge;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
	@Accessor(value = "context", remap = false)
	ICondition.IContext getContext();
}
