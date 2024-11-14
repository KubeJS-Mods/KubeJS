package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.client.KubeAnimatedParticle;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;

@RemapPrefixForJS("kjs$")
public interface ClientLevelKJS extends LevelKJS {
	@Override
	default ClientLevel kjs$self() {
		return (ClientLevel) this;
	}

	default KubeAnimatedParticle kubeParticle(double x, double y, double z, SpriteSet spriteSet) {
		return new KubeAnimatedParticle(kjs$self(), x, y, z, spriteSet);
	}
}
