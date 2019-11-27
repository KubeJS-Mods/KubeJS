package dev.latvian.kubejs.server;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

/**
 * @author LatvianModder
 */
public class ServerCommandSender extends CommandSource
{
	public ServerCommandSender(ServerJS w)
	{
		super(ICommandSource.field_213139_a_, Vec3d.ZERO, Vec2f.ZERO, (ServerWorld) w.getOverworld().minecraftWorld, 4, "Server", new StringTextComponent("Server"), w.minecraftServer, null, true, (context, success, result) -> {}, EntityAnchorArgument.Type.FEET);
	}
}