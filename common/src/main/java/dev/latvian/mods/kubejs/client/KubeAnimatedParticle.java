package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.mod.util.color.Color;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;

public class KubeAnimatedParticle extends SimpleAnimatedParticle {

	private Float2IntFunction lightColorFunction;

	public KubeAnimatedParticle(ClientLevel clientLevel, SpriteSet spriteSet, double x, double y, double z) {
		super(clientLevel, x, y, z, spriteSet, 0.0125F);
		setLifetime(20);
		setSpriteFromAge(spriteSet);
		lightColorFunction = super::getLightColor;
	}

	public void setGravity(float g) {
		this.gravity = g;
	}

	@Info(value = "Sets the friction value of the particle, the particle's motion is multiplied by this value every tick")
	public void setFriction(float f) {
		this.friction = f;
	}

	public void setColor(Color color, boolean alpha) {
		setColor(color.getRgbJS());
		if (alpha) {
			setAlpha((color.getArgbJS() >>> 24) / 255.0F);
		}
	}

	public void setColor(Color color) {
		setColor(color, false);
	}

	public void setPhysicality(boolean hasPhysics) {
		this.hasPhysics = hasPhysics;
	}

	public void setFasterWhenYMotionBlocked(boolean b) {
		speedUpWhenYMotionIsBlocked = b;
	}

	public void setLightColor(Float2IntFunction function) {
		lightColorFunction = function;
	}

	// Getters for protected values

	public ClientLevel getLevel() {
		return level;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double getXSpeed() {
		return xd;
	}

	public double getYSpeed() {
		return yd;
	}

	public double getZSpeed() {
		return zd;
	}

	public SpriteSet getSpriteSet() {
		return sprites;
	}

	public TextureAtlasSprite getSprite() {
		return sprite;
	}

	public RandomSource getRandom() {
		return random;
	}

	@Override
	public int getLightColor(float f) {
		return lightColorFunction.get(f);
	}
}
