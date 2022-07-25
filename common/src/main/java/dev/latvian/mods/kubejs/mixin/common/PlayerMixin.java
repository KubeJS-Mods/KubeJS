package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.PlayerKJS;
import dev.latvian.mods.kubejs.item.InventoryJS;
import dev.latvian.mods.kubejs.stages.Stages;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author LatvianModder
 */
@Mixin(value = Player.class, priority = 1001)
@RemapPrefixForJS("kjs$")
public abstract class PlayerMixin implements PlayerKJS {
	private Stages kjs$stages;
	private InventoryJS kjs$inventory;
	private AttachedData<Player> kjs$attachedData;

	@Override
	public Stages kjs$getStages() {
		if (kjs$stages == null) {
			kjs$stages = Stages.create(kjs$self());
		}

		return kjs$stages;
	}

	@Override
	public InventoryJS kjs$getInventory() {
		if (kjs$inventory == null) {
			kjs$inventory = new InventoryJS(kjs$self().getInventory()) {
				@Override
				public void markDirty() {
					kjs$sendInventoryUpdate();
				}
			};
		}

		return kjs$inventory;
	}

	@Override
	public AttachedData<Player> kjs$getData() {
		if (kjs$attachedData == null) {
			kjs$attachedData = new AttachedData<>(kjs$self());
			KubeJSPlugins.forEachPlugin(plugin -> plugin.attachPlayerData(kjs$attachedData));
		}

		return kjs$attachedData;
	}

	@Shadow
	@RemapForJS("closeMenu")
	public abstract void closeContainer();
}
