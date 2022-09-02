package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.EntityTargetKJS;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootContext.EntityTarget.class)
public abstract class EntityTargetMixin implements EntityTargetKJS {
	@Override
	@Accessor("name")
	public abstract String kjs$getName();
}
