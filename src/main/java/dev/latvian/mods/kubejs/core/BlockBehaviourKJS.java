package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.block.RandomTickCallbackJS;
import net.minecraft.world.level.block.SoundType;

import java.util.Map;
import java.util.function.Consumer;

public interface BlockBehaviourKJS {
	default Map<String, Object> kjs$getTypeData() {
		throw new NoMixinException();
	}

	default void kjs$setHasCollision(boolean v) {
		throw new NoMixinException();
	}

	default void kjs$setExplosionResistance(float v) {
		throw new NoMixinException();
	}

	default void kjs$setIsRandomlyTicking(boolean v) {
		throw new NoMixinException();
	}

	default void kjs$setRandomTickCallback(Consumer<RandomTickCallbackJS> callback) {
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
