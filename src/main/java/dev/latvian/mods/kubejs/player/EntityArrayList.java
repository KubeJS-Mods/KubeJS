package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.core.DataSenderKJS;
import dev.latvian.mods.kubejs.core.MessageSenderKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@RemapPrefixForJS("kjs$")
public class EntityArrayList extends ArrayList<Entity> implements MessageSenderKJS, DataSenderKJS {
	public final Level level;

	public EntityArrayList(Level l, int size) {
		super(size);
		level = l;
	}

	public EntityArrayList(Level l, Iterable<? extends Entity> entities) {
		this(l, entities instanceof Collection c ? c.size() : 4);
		addAllIterable(entities);
	}

	public void addAllIterable(Iterable<? extends Entity> entities) {
		if (entities instanceof Collection c) {
			addAll(c);
		} else {
			for (var entity : entities) {
				add(entity);
			}
		}
	}

	@Override
	public Component kjs$getName() {
		return Component.literal("EntityList");
	}

	@Override
	public Component kjs$getDisplayName() {
		return Component.literal(toString()).kjs$lightPurple();
	}

	@Override
	public void kjs$tell(Component message) {
		for (var entity : this) {
			entity.kjs$tell(message);
		}
	}

	@Override
	public void kjs$setStatusMessage(Component message) {
		for (var entity : this) {
			entity.kjs$setStatusMessage(message);
		}
	}

	@Override
	public void kjs$runCommand(String command) {
		for (var entity : this) {
			entity.kjs$runCommand(command);
		}
	}

	@Override
	public void kjs$runCommandSilent(String command) {
		for (var entity : this) {
			entity.kjs$runCommandSilent(command);
		}
	}

	@Override
	public void kjs$setActivePostShader(@Nullable ResourceLocation id) {
		for (var entity : this) {
			entity.kjs$setActivePostShader(id);
		}
	}

	public void kill() {
		for (var entity : this) {
			entity.kill();
		}
	}

	public void playSound(SoundEvent id, float volume, float pitch) {
		for (var entity : this) {
			entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), id, entity.getSoundSource(), volume, pitch);
		}
	}

	public void playSound(SoundEvent id) {
		playSound(id, 1F, 1F);
	}

	public EntityArrayList filter(List<Predicate<Entity>> filterList) {
		if (isEmpty() || filterList.isEmpty()) {
			return this;
		}

		var list = new EntityArrayList(level, size());

		for (var entity : this) {
			for (var filter : filterList) {
				if (filter.test(entity)) {
					list.add(entity);
				}
			}
		}

		return list;
	}

	public EntityArrayList filterSelector(EntitySelector selector) {
		return filter(selector.contextFreePredicates);
	}

	@Override
	public void kjs$sendData(String channel, @Nullable CompoundTag data) {
		for (var entity : this) {
			if (entity instanceof Player player) {
				player.kjs$sendData(channel, data);
			}
		}
	}

	@Override
	@Nullable
	public Entity getFirst() {
		return isEmpty() ? null : get(0);
	}
}