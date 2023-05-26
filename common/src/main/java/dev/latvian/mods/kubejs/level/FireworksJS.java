package dev.latvian.mods.kubejs.level;

import dev.latvian.mods.kubejs.core.FireworkRocketEntityKJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class FireworksJS {
	public static FireworksJS of(Object o) {
		var properties = MapJS.of(o);
		var fireworks = new FireworksJS();

		if (properties == null) {
			return fireworks;
		}

		if (properties.get("flight") instanceof Number flight) {
			fireworks.flight = flight.intValue();
		}

		if (properties.get("lifetime") instanceof Number lifetime) {
			fireworks.lifetime = lifetime.intValue();
		}

		if (properties.containsKey("explosions")) {
			for (var o1 : ListJS.orSelf(properties.get("explosions"))) {
				var m = MapJS.of(o1);

				if (m == null) {
					continue;
				}

				var e = new Explosion();

				if (m.get("shape") instanceof String shape) {
					e.shape = Shape.get(shape);
				}

				if (m.get("flicker") instanceof Boolean flicker) {
					e.flicker = flicker;
				}

				if (m.get("trail") instanceof Boolean trail) {
					e.trail = trail;
				}

				if (m.containsKey("colors")) {
					for (var o2 : ListJS.orSelf(m.get("colors"))) {
						e.colors.add(ColorWrapper.of(o2).getFireworkColorJS());
					}
				}

				if (m.containsKey("fadeColors")) {
					for (var o2 : ListJS.orSelf(m.get("fadeColors"))) {
						e.fadeColors.add(ColorWrapper.of(o2).getFireworkColorJS());
					}
				}

				if (e.colors.isEmpty()) {
					e.colors.add(ColorWrapper.YELLOW_DYE.getFireworkColorJS());
				}

				fireworks.explosions.add(e);
			}
		}

		if (fireworks.explosions.isEmpty()) {
			var e = new Explosion();
			e.colors.add(ColorWrapper.YELLOW_DYE.getFireworkColorJS());
			fireworks.explosions.add(e);
		}

		return fireworks;
	}

	public enum Shape {
		SMALL_BALL("small_ball", 0),
		LARGE_BALL("large_ball", 1),
		STAR("star", 2),
		CREEPER("creeper", 3),
		BURST("burst", 4);

		public static final Shape[] VALUES = values();

		private final String name;
		public final int type;

		Shape(String n, int t) {
			name = n;
			type = t;
		}

		public static Shape get(String name) {
			for (var s : VALUES) {
				if (s.name.equals(name)) {
					return s;
				}
			}

			return Shape.SMALL_BALL;
		}
	}

	public static class Explosion {
		public Shape shape = Shape.SMALL_BALL;
		public boolean flicker = false;
		public boolean trail = false;
		public final IntOpenHashSet colors = new IntOpenHashSet();
		public final IntOpenHashSet fadeColors = new IntOpenHashSet();
	}

	public int flight = 2;
	public int lifetime = -1;
	public final List<Explosion> explosions = new ArrayList<>();

	public FireworkRocketEntity createFireworkRocket(Level w, double x, double y, double z) {
		var stack = new ItemStack(Items.FIREWORK_ROCKET);

		var nbt = new CompoundTag();
		nbt.putInt("Flight", flight);
		var list = new ListTag();

		for (var e : explosions) {
			var nbt1 = new CompoundTag();
			nbt1.putInt("Type", e.shape.type);
			nbt1.putBoolean("Flicker", e.flicker);
			nbt1.putBoolean("Trail", e.trail);
			nbt1.putIntArray("Colors", e.colors.toIntArray());
			nbt1.putIntArray("FadeColors", e.fadeColors.toIntArray());
			list.add(nbt1);
		}

		nbt.put("Explosions", list);
		stack.addTagElement("Fireworks", nbt);

		var rocket = new FireworkRocketEntity(w, x, y, z, stack);

		if (lifetime != -1) {
			((FireworkRocketEntityKJS) rocket).setLifetimeKJS(lifetime);
		}

		rocket.setInvisible(true);
		return rocket;
	}
}