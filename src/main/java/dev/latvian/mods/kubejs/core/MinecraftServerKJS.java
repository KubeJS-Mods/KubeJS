package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.net.KubeJSNet;
import dev.latvian.mods.kubejs.net.SendDataFromServerPayload;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.server.ChangesForChat;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RemapPrefixForJS("kjs$")
public interface MinecraftServerKJS extends WithAttachedData<MinecraftServer>, WithPersistentData, DataSenderKJS, MinecraftEnvironmentKJS, EntityCollectionKJS {
	default MinecraftServer kjs$self() {
		return (MinecraftServer) this;
	}

	ServerLevel kjs$getOverworld();

	@Override
	default Component kjs$getName() {
		return Component.literal(kjs$self().name());
	}

	@Override
	default void kjs$tell(Component message) {
		kjs$self().sendSystemMessage(message);

		for (var player : kjs$self().getPlayerList().getPlayers()) {
			player.kjs$tell(message);
		}
	}

	@Override
	default void kjs$setStatusMessage(Component message) {
		for (var player : kjs$self().getPlayerList().getPlayers()) {
			player.kjs$setStatusMessage(message);
		}
	}

	@Override
	default void kjs$runCommand(String command) {
		kjs$self().getCommands().performPrefixedCommand(kjs$self().createCommandSourceStack(), command);
	}

	@Override
	default void kjs$runCommandSilent(String command) {
		kjs$self().getCommands().performPrefixedCommand(kjs$self().createCommandSourceStack().withSuppressedOutput(), command);
	}

	@Override
	default void kjs$setActivePostShader(@Nullable ResourceLocation id) {
		for (var player : kjs$self().getPlayerList().getPlayers()) {
			player.kjs$setActivePostShader(id);
		}
	}

	default ServerLevel kjs$getLevel(ResourceLocation dimension) {
		return kjs$self().getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
	}

	@Nullable
	default ServerPlayer kjs$getPlayer(PlayerSelector selector) {
		return selector.getPlayer(kjs$self());
	}

	@Override
	default Iterable<? extends Entity> kjs$getMcEntities() {
		var list = new ArrayList<Entity>(10);

		for (var level : kjs$self().getAllLevels()) {
			var i = level.getAllEntities();

			if (i instanceof Collection<Entity> c) {
				list.addAll(c);
			} else {
				for (var e : i) {
					list.add(e);
				}
			}
		}

		return list;
	}

	@Override
	default List<? extends Player> kjs$getMcPlayers() {
		return kjs$self().getPlayerList().getPlayers();
	}

	@Override
	default EntityArrayList kjs$getPlayers() {
		return new EntityArrayList(kjs$self().getPlayerList().getPlayers());
	}

	@Override
	@Nullable
	default Entity kjs$getEntityByUUID(UUID id) {
		for (var level : kjs$self().getAllLevels()) {
			var e = level.getEntities().get(id);

			if (e != null) {
				return e;
			}
		}

		return null;
	}

	@Override
	@Nullable
	default Entity kjs$getEntityByNetworkID(int id) {
		for (var level : kjs$self().getAllLevels()) {
			var e = level.getEntities().get(id);

			if (e != null) {
				return e;
			}
		}

		return null;
	}

	@Nullable
	default AdvancementNode kjs$getAdvancement(ResourceLocation id) {
		return kjs$self().getAdvancements().tree().get(id);
	}

	@Override
	default void kjs$sendData(String channel, @Nullable CompoundTag data) {
		KubeJSNet.sendToAllPlayers(new SendDataFromServerPayload(channel, data));
	}

	@HideFromJS
	default void kjs$afterResourcesLoaded(boolean reload) {
		if (reload) {
			DataExport.exportData();
		}

		ConsoleJS.SERVER.stopCapturingErrors();

		if (reload && CommonProperties.get().announceReload && !CommonProperties.get().hideServerScriptErrors) {
			if (ConsoleJS.SERVER.errors.isEmpty()) {
				kjs$tell(Component.literal("Reloaded with no KubeJS errors!").withStyle(ChatFormatting.GREEN));
			} else {
				kjs$tell(ConsoleJS.SERVER.errorsComponent("/kubejs errors server"));
			}

			if (DevProperties.get().logChangesInChat) {
				ChangesForChat.print(this::kjs$tell);
			}
		}

		ConsoleJS.SERVER.info("Server resource reload complete!");
	}

	default Map<UUID, Map<Integer, ItemStack>> kjs$restoreInventories() {
		throw new NoMixinException();
	}
}
