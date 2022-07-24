package dev.latvian.mods.kubejs.level;

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
public class WorldlyCommandSender extends CommandSourceStack {
	public WorldlyCommandSender(ServerLevelJS l) {
		super(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, l.getMinecraftLevel(), 4, l.getDimension().toString(), Component.literal(l.getDimension().toString()), l.getServer(), null, true, (context, success, result) ->
		{
		}, EntityAnchorArgument.Anchor.FEET, CommandSigningContext.NONE);
	}
}