package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.block.callback.RandomTickCallback;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.level.block.SoundType;

import java.util.function.Consumer;

@RemapPrefixForJS("kjs$")
public interface BlockBehaviourKJS extends BlockProviderKJS {
	default void kjs$setHasCollision(boolean v) {
		throw new NoMixinException();
	}

	default void kjs$setExplosionResistance(float v) {
		throw new NoMixinException();
	}

	default void kjs$setIsRandomlyTicking(boolean v) {
		throw new NoMixinException();
	}

	default void kjs$setRandomTickCallback(Consumer<RandomTickCallback> callback) {
		throw new NoMixinException();
	}

	default void kjs$setSoundType(SoundType v) {
		throw new NoMixinException();
	}

	default void kjs$setFriction(float v) {
		throw new NoMixinException();
	}

	default void kjs$setSpeedFactor(float v) {
		throw new NoMixinException();
	}

	default void kjs$setJumpFactor(float v) {
		throw new NoMixinException();
	}
}
