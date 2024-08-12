package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.event.KubeStartupEvent;
import dev.latvian.mods.kubejs.item.MutableToolTier;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

import java.util.Map;
import java.util.function.Consumer;

@Info("""
	Invoked when the game is starting up and the item tool tiers are being registered.
	""")
public record ItemToolTierRegistryKubeEvent(Map<String, Tier> tiers) implements KubeStartupEvent {
	@Info("Adds a new tool tier.")
	public void add(String id, Consumer<MutableToolTier> tier) {
		var t = new MutableToolTier(Tiers.IRON);
		tier.accept(t);
		tiers.put(id, t);
	}

	public void addBasedOnExisting(String id, String existing, Consumer<MutableToolTier> tier) {
		var t = new MutableToolTier(tiers.getOrDefault(existing, Tiers.IRON));
		tier.accept(t);
		tiers.put(id, t);
	}

	public void addExisting(String id, Tier tier) {
		tiers.put(id, tier);
	}
}