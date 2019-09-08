package dev.latvian.kubejs.world;

import dev.latvian.kubejs.text.TextColor;
import dev.latvian.kubejs.util.UtilsJS;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class FireworksJS
{
	private static final int[] DYE_COLORS = {16383998, 16351261, 13061821, 3847130, 16701501, 8439583, 15961002, 4673362, 10329495, 1481884, 8991416, 3949738, 8606770, 6192150, 11546150, 1908001};

	public static FireworksJS of(Map<String, Object> properties)
	{
		FireworksJS fireworks = new FireworksJS();

		if (properties.get("flight") instanceof Number)
		{
			fireworks.flight = ((Number) properties.get("flight")).intValue();
		}

		if (properties.get("lifeTime") instanceof Number)
		{
			fireworks.lifeTime = ((Number) properties.get("lifeTime")).intValue();
		}

		if (properties.containsKey("explosions"))
		{
			for (Object o : UtilsJS.getList(properties.get("explosions")))
			{
				if (o instanceof Map)
				{
					Map m = (Map) o;
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
						for (Object o1 : UtilsJS.getList(m.get("colors")))
						{
							if (o1 instanceof Number)
							{
								e.colors.add(((Number) o1).intValue());
							}
							else if (o1 instanceof TextColor)
							{
								e.colors.add(((TextColor) o1).color);
							}
							else if (o1 instanceof String)
							{
								e.colors.add(DYE_COLORS[EnumDyeColor.valueOf(o1.toString()).getMetadata()]);
							}
						}
					}

					if (m.containsKey("fadeColors"))
					{
						for (Object o1 : UtilsJS.getList(m.get("fadeColors")))
						{
							if (o1 instanceof Number)
							{
								e.fadeColors.add(((Number) o1).intValue());
							}
							else if (o1 instanceof TextColor)
							{
								e.fadeColors.add(((TextColor) o1).color);
							}
							else if (o1 instanceof String)
							{
								e.fadeColors.add(DYE_COLORS[EnumDyeColor.valueOf(o1.toString()).getMetadata()]);
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
	public int lifeTime = -1;
	public final List<Explosion> explosions = new ArrayList<>();

	public EntityFireworkRocket createFireworkRocket(World w, double x, double y, double z)
	{
		ItemStack stack = new ItemStack(Items.FIREWORKS);

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("Flight", flight);
		NBTTagList list = new NBTTagList();

		for (Explosion e : explosions)
		{
			NBTTagCompound nbt1 = new NBTTagCompound();
			nbt1.setInteger("Type", e.shape.type);
			nbt1.setBoolean("Flicker", e.flicker);
			nbt1.setBoolean("Trail", e.trail);
			nbt1.setIntArray("Colors", e.colors.toIntArray());
			nbt1.setIntArray("FadeColors", e.fadeColors.toIntArray());
			list.appendTag(nbt1);
		}

		nbt.setTag("Explosions", list);
		stack.setTagInfo("Fireworks", nbt);

		EntityFireworkRocket rocket = new EntityFireworkRocket(w, x, y, z, stack);

		if (lifeTime != -1)
		{
			NBTTagCompound entityNbt = new NBTTagCompound();
			rocket.writeEntityToNBT(entityNbt);
			entityNbt.setInteger("LifeTime", lifeTime);
			rocket.readEntityFromNBT(entityNbt);
		}

		rocket.setInvisible(true);
		return rocket;
	}
}