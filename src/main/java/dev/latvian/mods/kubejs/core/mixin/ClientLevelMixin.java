package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ClientLevelKJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.UUID;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements ClientLevelKJS {
	@Shadow
	@Final
	@HideFromJS
	List<AbstractClientPlayer> players;

	@Shadow
	@HideFromJS
	public abstract List<AbstractClientPlayer> players();

	@Shadow
	@HideFromJS
	protected abstract LevelEntityGetter<Entity> getEntities();

	@Override
	@Nullable
	public Entity kjs$getEntityByUUID(UUID id) {
		return getEntities().get(id);
	}
}
