package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.Entity;

@RemapPrefixForJS("kjs$")
public interface EntityKJS extends AsKJS<EntityJS>, WithPersistentData {
	@Override
	default EntityJS asKJS() {
		var entity = (Entity) this;
		return KubeJS.PROXY.getLevel(entity.level).getEntity(entity);
	}

	default Entity kjs$self() {
		throw new NoMixinException();
	}
}
