package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.core.PlayerKJS;
import dev.latvian.mods.kubejs.player.KubeJSInventoryListener;
import dev.latvian.mods.kubejs.stages.StageEvents;
import dev.latvian.mods.kubejs.stages.Stages;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Player.class, priority = 1001)
@RemapPrefixForJS("kjs$")
public abstract class PlayerMixin implements PlayerKJS {
	@Unique
	private Stages kjs$stages;

	@Unique
	private AttachedData<Player> kjs$attachedData;

	@Unique
	private KubeJSInventoryListener kjs$inventoryChangeListener;

	@Override
	public Stages kjs$getStages() {
		if (kjs$stages == null) {
			kjs$stages = StageEvents.create(kjs$self());
		}

		return kjs$stages;
	}

	@Override
	public InventoryKJS kjs$getInventory() {
		return kjs$self().getInventory();
	}

	@Override
	public InventoryKJS kjs$getCraftingGrid() {
		return kjs$self().inventoryMenu.getCraftSlots();
	}

	@Override
	public AttachedData<Player> kjs$getData() {
		if (kjs$attachedData == null) {
			kjs$attachedData = new AttachedData<>(kjs$self());
			KubeJSPlugins.forEachPlugin(kjs$attachedData, KubeJSPlugin::attachPlayerData);
		}

		return kjs$attachedData;
	}

	@Shadow
	@RemapForJS("closeMenu")
	public abstract void closeContainer();

	@Override
	public KubeJSInventoryListener kjs$getInventoryChangeListener() {
		if (kjs$inventoryChangeListener == null) {
			kjs$inventoryChangeListener = new KubeJSInventoryListener((Player) (Object) this);
		}

		return kjs$inventoryChangeListener;
	}
}
