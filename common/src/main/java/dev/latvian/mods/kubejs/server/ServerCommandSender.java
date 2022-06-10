package dev.latvian.mods.kubejs.server;

import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * @author LatvianModder
 */
public class ServerCommandSender extends CommandSourceStack {
	public ServerCommandSender(ServerJS w) {
		super(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, w.getOverworld().getMinecraftLevel(), 4, "Server", Component.literal("Server"), w.getMinecraftServer(), null, true, (commandContext, bl, ix) -> {
		}, EntityAnchorArgument.Anchor.FEET, CommandSigningContext.NONE);
	}
}