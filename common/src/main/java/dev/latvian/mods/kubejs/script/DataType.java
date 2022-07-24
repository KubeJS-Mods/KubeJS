package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.core.WithAttachedData;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.player.PlayerDataJS;
import dev.latvian.mods.kubejs.player.PlayerJS;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.minecraft.server.MinecraftServer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public record DataType<T extends WithAttachedData>(
		String name,
		Class<T> parent,
		Class actualParent,
		Consumer<AttachDataEvent<T>> pluginCallback
) {

	public static DataType<MinecraftServer> SERVER = new DataType<>("server", MinecraftServer.class, forEachPlugin(KubeJSPlugin::attachServerData));
	public static DataType<LevelJS> LEVEL = new DataType<>("leve", LevelJS.class, forEachPlugin(KubeJSPlugin::attachLevelData));
	public static DataType<PlayerDataJS> PLAYER = new DataType<>("player", PlayerDataJS.class, PlayerJS.class, forEachPlugin(KubeJSPlugin::attachPlayerData));

	public DataType(String s, Class<T> c, Consumer<AttachDataEvent<T>> cb) {
		this(s, c, c, cb);
	}

	public static <U extends WithAttachedData> Consumer<AttachDataEvent<U>> forEachPlugin(BiConsumer<KubeJSPlugin, AttachDataEvent<U>> c) {
		return event -> KubeJSPlugins.forEachPlugin(plugin -> c.accept(plugin, event));
	}
}