package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

public class DamageSourceWrapper {
	public static DamageSource wrap(Context cx, @Nullable Object from) {
		var registries = RegistryAccessContainer.of(cx);
		return switch (from) {
			case DamageSource source -> source;
			case Player player -> registries.damageSources().playerAttack(player);
			case LivingEntity livingEntity -> registries.damageSources().mobAttack(livingEntity);
			case null -> throw new KubeRuntimeException("Can't convert null to damage source!").source(SourceLine.of(cx));
			default -> registries.damageSources().source(ResourceKey.create(Registries.DAMAGE_TYPE, ID.mc(from)));
		};
	}
}
