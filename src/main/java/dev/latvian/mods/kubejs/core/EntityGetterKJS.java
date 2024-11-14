package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.phys.AABB;

import java.util.List;

@RemapPrefixForJS("kjs$")
public interface EntityGetterKJS extends EntityCollectionKJS {
	default EntityGetter kjs$self() {
		return (EntityGetter) this;
	}

	@Override
	default List<? extends Player> kjs$getMcPlayers() {
		return kjs$self().players();
	}

	@Override
	default EntityArrayList kjs$getPlayers() {
		return new EntityArrayList(kjs$self().players());
	}

	@Override
	default Iterable<? extends Entity> kjs$getMcEntities() {
		return kjs$self().getEntities((Entity) null, AABB.INFINITE, EntityArrayList.ALWAYS_TRUE_PREDICATE);
	}

	@Override
	default EntityArrayList kjs$getEntitiesWithin(AABB aabb) {
		return new EntityArrayList(kjs$self().getEntities(null, aabb));
	}
}
