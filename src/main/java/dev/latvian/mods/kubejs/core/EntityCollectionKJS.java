package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RemapPrefixForJS("kjs$")
public interface EntityCollectionKJS {
	Iterable<? extends Entity> kjs$getMcEntities();

	default List<? extends Player> kjs$getMcPlayers() {
		var list = new ArrayList<Player>(10);

		for (var entity : kjs$getMcEntities()) {
			if (entity instanceof Player p) {
				list.add(p);
			}
		}

		return list;
	}

	default EntityArrayList kjs$getPlayers() {
		return new EntityArrayList(kjs$getMcPlayers());
	}

	default EntityArrayList kjs$getEntities() {
		return new EntityArrayList(kjs$getMcEntities());
	}

	default EntityArrayList kjs$getEntitiesWithin(AABB aabb) {
		if (aabb == null || aabb == AABB.INFINITE) {
			return kjs$getEntities();
		}

		var list = new EntityArrayList(10);

		for (var entity : kjs$getMcEntities()) {
			if (entity.getBoundingBox().intersects(aabb)) {
				list.add(entity);
			}
		}

		return list;
	}

	@Nullable
	default Entity kjs$getEntityByUUID(UUID id) {
		for (var entity : kjs$getMcEntities()) {
			if (entity.getUUID().equals(id)) {
				return entity;
			}
		}

		return null;
	}

	@Nullable
	default Entity kjs$getEntityByNetworkID(int id) {
		for (var entity : kjs$getMcEntities()) {
			if (entity.getId() == id) {
				return entity;
			}
		}

		return null;
	}
}
