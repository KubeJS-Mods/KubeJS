package dev.latvian.mods.kubejs.level;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * @author LatvianModder
 */
public class WorldlyCommandSender extends CommandSourceStack {
	public WorldlyCommandSender(ServerLevelJS l) {
		super(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, l.getMinecraftLevel(), 4, l.getDimension(), new TextComponent(l.getDimension()), l.getServer().getMinecraftServer(), null, true, (context, success, result) ->
		{
		}, EntityAnchorArgument.Anchor.FEET);
	}
}