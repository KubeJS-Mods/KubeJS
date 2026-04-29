package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.core.DataSenderKJS;
import dev.latvian.mods.kubejs.core.MessageSenderKJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@RemapPrefixForJS("kjs$")
public class EntityArrayList extends ArrayList<Entity> implements MessageSenderKJS, DataSenderKJS {
	public static final Predicate<Entity> ALWAYS_TRUE_PREDICATE = entity -> true;

	public EntityArrayList(int size) {
		super(size);
	}

	public EntityArrayList(Iterable<? extends Entity> entities) {
		this(entities instanceof Collection c ? c.size() : 4);
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
	@Info(value = "Sends a message in chat to every entity in the list.", params = {
		@Param(name = "message", value = "A text component. It may be a string, which will be implicitly wrapped into a text component."),
	})
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
	@Info(value = "Each entity in the list runs the specified console command with their permission level.", params = {
		@Param(name = "command", value = "The console command. Slash at the beginning is optional."),
	})
	public void kjs$runCommand(String command) {
		for (var entity : this) {
			entity.kjs$runCommand(command);
		}
	}

	@Override
	@Info(value = "Each entity in the list runs the specified console command with their permission level. The command won't output any logs in chat nor console", params = {
		@Param(name = "command", value = "The console command. Slash at the beginning is optional."),
	})
	public void kjs$runCommandSilent(String command) {
		for (var entity : this) {
			entity.kjs$runCommandSilent(command);
		}
	}

	@Override
	public void kjs$setActivePostShader(@Nullable Identifier id) {
		for (var entity : this) {
			entity.kjs$setActivePostShader(id);
		}
	}

	@Info("Kills every entity in the list.")
	public void kill() {
		for (var entity : this) {
			entity.kill((ServerLevel) entity.level());
		}
	}

	@Info("Plays a sound from each entity in the list, unless the entity is silent.")
	public void playSound(SoundEvent id, float volume, float pitch) {
		for (var entity : this) {
			entity.playSound(id, volume, pitch);
		}
	}

	@Info("Plays a sound from each entity in the list, unless the entity is silent.")
	public void playSound(SoundEvent id) {
		playSound(id, 1F, 1F);
	}

	@Info(value = """
		Filters the entity list by passing each entity through a given predicate.
		Entities that pass the predicate will end up in the resulting entity list.
		""", params = {
		@Param(name = "filter", value = "The predicate - a function that takes an argument of `Entity` and returns a boolean.")
	})
	// FIXME: Inaccessible from JS due to order of operations in Rhino, this method is used by other filter methods
	public EntityArrayList filter(Predicate<Entity> filter) {
		if (isEmpty()) {
			return this;
		}

		var list = new EntityArrayList(size() / 4);

		for (var entity : this) {
			if (filter.test(entity)) {
				list.add(entity);
			}
		}

		return list;
	}

	@Info(value = """
		Filters the entity list by passing each entity through all predicates in provided list.
		Entities that pass at least one of the predicates will end up in the resulting entity list.
		""", params = {
		@Param(name = "filterList", value = "The list of predicates - functions that take one argument of `Entity` and return boolean values.")
	})
	public EntityArrayList filterList(List<Predicate<Entity>> filterList) {
		if (isEmpty() || filterList.isEmpty()) {
			return this;
		}

		var list = new EntityArrayList(size());

		for (var entity : this) {
			for (var filter : filterList) {
				if (filter.test(entity)) {
					list.add(entity);
				}
			}
		}

		return list;
	}

	@Info(value = "Filters the entity list based on the provided `EntitySelector`.", params = {
		@Param(name = "selector", value = "The entity selector. It may be a string representing the entity selector as seen in commands, such as `'@e[distance=..25]'`")
	})
	public EntityArrayList filterSelector(EntitySelector selector) {
		return filterList(selector.contextFreePredicates);
	}

	@Info(value = """
		Filters the entity list based on distance to the given point.
		Entities that are closer than `distance` away from the point specified by `x`, `y` and `z` coordinates will end up in the resulting list.
		""", params = {
		@Param(name = "x", value = "The `x` coordinate of the point."),
		@Param(name = "y", value = "The `y` coordinate of the point."),
		@Param(name = "z", value = "The `z` coordinate of the point."),
		@Param(name = "distance", value = "The maximum distance of entities from the point.")
	})
	public EntityArrayList filterDistance(double x, double y, double z, double distance) {
		var list = new EntityArrayList(size());

		for (var entity : this) {
			if (entity.distanceToSqr(x, y, z) <= distance * distance) {
				list.add(entity);
			}
		}

		return list;
	}

	@Info(value = """
		Filters the entity list based on distance to the given block position.
		Entities that are closer than `distance` away from the center of the block will end up in the resulting list.
		""", params = {
		@Param(name = "pos", value = "The `BlockPos` - that is the center of the block at specified position. It can be a 3-element array of integers, such as `[64, 25, 39]`."),
		@Param(name = "distance", value = "The maximum distance of entities from the point.")
	})
	public EntityArrayList filterDistance(BlockPos pos, double distance) {
		return filterDistance(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, distance);
	}

	@Info("Results in an entity list containing only players.")
	public EntityArrayList filterPlayers() {
		return filter(e -> e instanceof Player);
	}

	@Info("Results in an entity list containing only item entities.")
	public EntityArrayList filterItems() {
		return filter(e -> e instanceof ItemEntity);
	}

	@Info(value = "Filters the entity list based on the type of the entity. Only entities whose type is equal to the provided one will end up in the resulting list.", params = {
		@Param(name = "type", value = "The entity type. It may be a string representing an entity ID, like `'minecraft:creeper'`.")
	})
	public EntityArrayList filterType(EntityType<?> type) {
		return filter(e -> e.getType() == type);
	}

	@Override
	@Info(value = "Sends NBT data to every player in the list.", params = {
		@Param(name = "channel", value = "String. Represents the network channel."),
		@Param(name = "data", value = """
			The NBT compound tag containing data to send. May be `null`.
			It may be a JS object containing data or string representing stringified NBT.
			""")
	})
	public void kjs$sendData(String channel, @Nullable CompoundTag data) {
		for (var entity : this) {
			if (entity instanceof Player player) {
				player.kjs$sendData(channel, data);
			}
		}
	}

	@Override
	@Nullable
	@Info("Gets the first entity on the list, or `null` if the list is empty.")
	public Entity getFirst() {
		return isEmpty() ? null : get(0);
	}
}
