package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BasicCommandKubeEvent implements KubeEntityEvent {
	private final CommandSourceStack source;
	private final Level level;
	private final Entity entity;
	private final ServerPlayer serverPlayer;
	private final BlockPos pos;
	public final String id;
	public final String input;

	public BasicCommandKubeEvent(CommandSourceStack source, String id, String input) {
		this.source = source;
		this.level = source.getLevel();
		this.entity = source.getEntity();
		this.serverPlayer = entity instanceof ServerPlayer p ? p : null;
		this.pos = BlockPos.containing(source.getPosition());
		this.id = id;
		this.input = input;
	}

	public String getId() {
		return id;
	}

	@Override
	public Level getLevel() {
		return level;
	}

	@Override
	@Nullable
	public Entity getEntity() {
		return entity;
	}

	@Override
	@Nullable
	public ServerPlayer getPlayer() {
		return serverPlayer;
	}

	public LevelBlock getBlock() {
		return this.getLevel().kjs$getBlock(pos);
	}

	public void respondLazily(Supplier<Component> text, boolean informAdmins) {
		source.sendSuccess(text, informAdmins);
	}

	public void respond(Component text) {
		respondLazily(() -> text, false);
	}
}