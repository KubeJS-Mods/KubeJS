package dev.latvian.kubejs.world;

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
public class WorldCommandSender extends CommandSource
{
	public WorldCommandSender(ServerWorldJS w)
	{
		super(ICommandSource.DUMMY, Vec3d.ZERO, Vec2f.ZERO, (ServerWorld) w.minecraftWorld, 4, "World", new StringTextComponent("World"), w.getServer().minecraftServer, null, true, (context, success, result) -> {}, EntityAnchorArgument.Type.FEET);
	}
}