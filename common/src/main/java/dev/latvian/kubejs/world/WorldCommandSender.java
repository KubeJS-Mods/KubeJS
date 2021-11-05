package dev.latvian.kubejs.world;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * @author LatvianModder
 */
public class WorldCommandSender extends CommandSourceStack {
	public WorldCommandSender(ServerWorldJS w) {
		super(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, (ServerLevel) w.minecraftLevel, 4, "World", new TextComponent("World"), w.getServer().getMinecraftServer(), null, true, (context, success, result) ->
		{
		}, EntityAnchorArgument.Anchor.FEET);
	}
}