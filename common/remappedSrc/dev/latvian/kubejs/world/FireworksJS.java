package dev.latvian.kubejs.world;

import dev.latvian.kubejs.core.FireworkRocketEntityKJS;
import dev.latvian.kubejs.text.TextColor;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * @author LatvianModder
 */
public class FireworksJS
{
	public static FireworksJS of(Object o)
	{
		MapJS properties = MapJS.of(o);
		FireworksJS fireworks = new FireworksJS();

		if (properties == null)
		{
			return fireworks;
		}

		if (properties.get("flight") instanceof Number)
		{
			fireworks.flight = ((Number) properties.get("flight")).intValue();
		}

		if (properties.get("lifeTime") instanceof Number)
		{
			fireworks.lifetime = ((Number) properties.get("lifeTime")).intValue();
		}

		if (properties.containsKey("explosions"))
		{
			for (Object o1 : ListJS.orSelf(properties.get("explosions")))
			{
				MapJS m = MapJS.of(o1);

				if (m == null)
				{
					continue;
				}

				Explosion e = new Explosion();

				if (m.get("shape") instanceof String)
				{
					e.shape = Shape.get(m.get("shape").toString());
				}

				if (m.get("flicker") instanceof Boolean)
				{
					e.flicker = (Boolean) m.get("flicker");
				}

				if (m.get("trail") instanceof Boolean)
				{
					e.trail = (Boolean) m.get("trail");
				}

				if (m.containsKey("colors"))
				{
					for (Object o2 : ListJS.orSelf(m.get("colors")))
					{
						if (o2 instanceof Number)
						{
							e.colors.add(((Number) o2).intValue());
						}
						else if (o2 instanceof TextColor)
						{
							e.colors.add(((TextColor) o2).color);
						}
						else if (o2 instanceof String)
						{
							e.colors.add(DyeColor.valueOf(o2.toString()).getColorValue());
						}
					}
				}

				if (m.containsKey("fadeColors"))
				{
					for (Object o2 : ListJS.orSelf(m.get("fadeColors")))
					{
						if (o2 instanceof Number)
						{
							e.fadeColors.add(((Number) o2).intValue());
						}
						else if (o2 instanceof TextColor)
						{
							e.fadeColors.add(((TextColor) o2).color);
						}
						else if (o2 instanceof String)
						{
							e.fadeColors.add(DyeColor.valueOf(o2.toString()).getColorValue());
						}
					}
				}

				if (e.colors.isEmpty())
				{
					e.colors.add(TextColor.YELLOW.color);
				}

				fireworks.explosions.add(e);
			}
		}

		if (fireworks.explosions.isEmpty())
		{
			Explosion e = new Explosion();
			e.colors.add(TextColor.YELLOW.color);
			fireworks.explosions.add(e);
		}

		return fireworks;
	}

	public enum Shape
	{
		SMALL_BALL("small_ball", 0),
		LARGE_BALL("large_ball", 1),
		STAR("star", 2),
		CREEPER("creeper", 3),
		BURST("burst", 4);

		public static final Shape[] VALUES = values();

		private final String name;
		public final int type;

		Shape(String n, int t)
		{
			name = n;
			type = t;
		}

		public static Shape get(String name)
		{
			for (Shape s : VALUES)
			{
				if (s.name.equals(name))
				{
					return s;
				}
			}

			return Shape.SMALL_BALL;
		}
	}

	public static class Explosion
	{
		public Shape shape = Shape.SMALL_BALL;
		public boolean flicker = false;
		public boolean trail = false;
		public final IntOpenHashSet colors = new IntOpenHashSet();
		public final IntOpenHashSet fadeColors = new IntOpenHashSet();
	}

	public int flight = 2;
	public int lifetime = -1;
	public final List<Explosion> explosions = new ArrayList<>();

	public FireworkRocketEntity createFireworkRocket(Level w, double x, double y, double z)
	{
		ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);

		CompoundTag nbt = new CompoundTag();
		nbt.putInt("Flight", flight);
		ListTag list = new ListTag();

		for (Explosion e : explosions)
		{
			CompoundTag nbt1 = new CompoundTag();
			nbt1.putInt("Type", e.shape.type);
			nbt1.putBoolean("Flicker", e.flicker);
			nbt1.putBoolean("Trail", e.trail);
			nbt1.putIntArray("Colors", e.colors.toIntArray());
			nbt1.putIntArray("FadeColors", e.fadeColors.toIntArray());
			list.add(nbt1);
		}

		nbt.put("Explosions", list);
		stack.addTagElement("Fireworks", nbt);

		FireworkRocketEntity rocket = new FireworkRocketEntity(w, x, y, z, stack);

		if (lifetime != -1)
		{
			((FireworkRocketEntityKJS) rocket).setLifetimeKJS(lifetime);
		}

		rocket.setInvisible(true);
		return rocket;
	}
}