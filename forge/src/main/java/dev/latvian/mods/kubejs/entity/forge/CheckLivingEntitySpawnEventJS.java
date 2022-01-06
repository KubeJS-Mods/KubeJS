package dev.latvian.mods.kubejs.entity.forge;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.entity.LivingEntityEventJS;
import dev.latvian.mods.kubejs.level.world.BlockContainerJS;
import dev.latvian.mods.kubejs.level.world.LevelJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

/**
 * @author LatvianModder
 */
public class CheckLivingEntitySpawnEventJS extends LivingEntityEventJS {
	private final LivingSpawnEvent.CheckSpawn event;

	public CheckLivingEntitySpawnEventJS(LivingSpawnEvent.CheckSpawn e) {
		event = e;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public LevelJS getLevel() {
		return levelOf((Level) event.getWorld());
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(event.getEntity());
	}

	public double getX() {
		return event.getX();
	}

	public double getY() {
		return event.getY();
	}

	public double getZ() {
		return event.getZ();
	}

	public BlockContainerJS getBlock() {
		return new BlockContainerJS((Level) event.getWorld(), new BlockPos(getX(), getY(), getZ()));
	}
}