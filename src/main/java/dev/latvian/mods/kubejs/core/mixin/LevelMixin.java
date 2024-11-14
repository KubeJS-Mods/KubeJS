package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.LevelKJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(Level.class)
@RemapPrefixForJS("kjs$")
public abstract class LevelMixin implements LevelKJS {
	@Unique
	private AttachedData<Level> kjs$attachedData;

	@Override
	public AttachedData<Level> kjs$getData() {
		if (kjs$attachedData == null) {
			kjs$attachedData = new AttachedData<>(kjs$self());
			KubeJSPlugins.forEachPlugin(kjs$attachedData, KubeJSPlugin::attachLevelData);
		}

		return kjs$attachedData;
	}

	@Shadow
	@RemapForJS("getTime")
	public abstract long getGameTime();

	@Shadow
	@RemapForJS("getDimensionKey")
	public abstract ResourceKey<Level> dimension();

	@Shadow
	@HideFromJS
	protected abstract LevelEntityGetter<Entity> getEntities();

	@Override
	public Iterable<? extends Entity> kjs$getMcEntities() {
		return getEntities().getAll();
	}

	@Override
	@Nullable
	public Entity kjs$getEntityByUUID(UUID id) {
		return getEntities().get(id);
	}

	@Override
	@Nullable
	public Entity kjs$getEntityByNetworkID(int id) {
		return getEntities().get(id);
	}
}
