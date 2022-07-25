package dev.latvian.mods.kubejs.level;

import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * @author LatvianModder
 */
public class WorldlyCommandSender extends CommandSourceStack {
	public WorldlyCommandSender(ServerLevel l) {
		super(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, l, 4, l.dimension().location().toString(), Component.literal(l.dimension().location().toString()), l.getServer(), null, true, (context, success, result) ->
		{
		}, EntityAnchorArgument.Anchor.FEET, CommandSigningContext.NONE);
	}
}