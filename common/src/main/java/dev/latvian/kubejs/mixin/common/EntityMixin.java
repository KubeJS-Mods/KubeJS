package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.core.AsKJS;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class EntityMixin implements AsKJS {
	@Override
	public Object asKJS() {
		Entity entity = (Entity) (Object) this;
		return KubeJS.PROXY.getWorld(entity.level).getEntity(entity);
	}
}
