package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.core.LevelKJS;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelKJS {
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
}
