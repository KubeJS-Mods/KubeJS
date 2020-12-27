package dev.latvian.kubejs.entity.forge;

import dev.latvian.kubejs.util.MapJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class EntityJSImpl
{
	public static MapJS getPersistentData(Entity entity)
	{
		if (!(entity instanceof Player))
		{
			CompoundTag nbt = entity.getPersistentData();
			MapJS map = MapJS.of(nbt.get("KubeJS"));

			if (map == null)
			{
				map = new MapJS();
			}

			map.changeListener = o ->
			{
				CompoundTag n = MapJS.nbt(o);

				if (n != null)
				{
					entity.getPersistentData().put("KubeJS", n);
				}
			};

			return map;
		}
		CompoundTag nbt = entity.getPersistentData();
		CompoundTag nbt1 = (CompoundTag) nbt.get(Player.PERSISTED_NBT_TAG);
		MapJS map = MapJS.of(nbt1 == null ? null : nbt1.get("KubeJS"));

		if (map == null)
		{
			map = new MapJS();
		}

		map.changeListener = m ->
		{
			CompoundTag n = MapJS.nbt(m);

			if (n != null)
			{
				CompoundTag n1 = entity.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
				n1.put("KubeJS", n);
				entity.getPersistentData().put(Player.PERSISTED_NBT_TAG, n1);
			}
		};

		return map;
	}
}
