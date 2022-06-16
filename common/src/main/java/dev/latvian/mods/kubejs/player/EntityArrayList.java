package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.util.MessageSender;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class EntityArrayList extends ArrayList<EntityJS> implements MessageSender {
	private final LevelJS level;

	public EntityArrayList(LevelJS l, int size) {
		super(size);
		level = l;
	}

	public EntityArrayList(LevelJS l, Iterable<? extends Entity> entities) {
		this(l, entities instanceof Collection c ? c.size() : 10);

		for (var entity : entities) {
			add(level.getEntity(entity));
		}
	}

	public LevelJS getLevel() {
		return level;
	}

	@Override
	public Component getName() {
		return new TextComponent("EntityList");
	}

	@Override
	public Component getDisplayName() {
		return new TextComponent(toString()).lightPurple();
	}

	@Override
	public void tell(Component message) {
		for (var entity : this) {
			entity.minecraftEntity.sendMessage(message, Util.NIL_UUID);
		}
	}

	@Override
	public void setStatusMessage(Component message) {
		for (var entity : this) {
			if (entity.minecraftEntity instanceof ServerPlayer player) {
				player.displayClientMessage(message, true);
			}
		}
	}

	@Override
	public int runCommand(String command) {
		var m = 0;

		for (var entity : this) {
			m = Math.max(m, entity.runCommand(command));
		}

		return m;
	}

	@Override
	public int runCommandSilent(String command) {
		var m = 0;

		for (var entity : this) {
			m = Math.max(m, entity.runCommandSilent(command));
		}

		return m;
	}

	public void kill() {
		for (var entity : this) {
			entity.kill();
		}
	}

	public void playSound(SoundEvent id, float volume, float pitch) {
		for (var entity : this) {
			entity.minecraftEntity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), id, entity.minecraftEntity.getSoundSource(), volume, pitch);
		}
	}

	public void playSound(SoundEvent id) {
		playSound(id, 1F, 1F);
	}

	public EntityArrayList filter(Predicate<EntityJS> filter) {
		if (isEmpty()) {
			return this;
		}

		var list = new EntityArrayList(level, size());

		for (var entity : this) {
			if (filter.test(entity)) {
				list.add(entity);
			}
		}

		return list;
	}

	public void sendData(String channel, @Nullable CompoundTag data) {
		for (var entity : this) {
			if (entity instanceof PlayerJS playerJS) {
				playerJS.sendData(channel, data);
			}
		}
	}

	public EntityJS getFirst() {
		return get(0);
	}
}