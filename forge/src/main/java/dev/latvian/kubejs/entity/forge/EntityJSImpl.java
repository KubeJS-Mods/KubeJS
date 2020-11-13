package dev.latvian.kubejs.entity.forge;

import dev.latvian.kubejs.util.MapJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class EntityJSImpl
{
	public static MapJS getPersistentData(Entity entity)
	{
		if (!(entity instanceof PlayerEntity))
		{
			CompoundNBT nbt = entity.getPersistentData();
			MapJS map = MapJS.of(nbt.get("KubeJS"));

			if (map == null)
			{
				map = new MapJS();
			}

			map.changeListener = o ->
			{
				CompoundNBT n = MapJS.nbt(o);

				if (n != null)
				{
					entity.getPersistentData().put("KubeJS", n);
				}
			};

			return map;
		}
		CompoundNBT nbt = entity.getPersistentData();
		CompoundNBT nbt1 = (CompoundNBT) nbt.get(PlayerEntity.PERSISTED_NBT_TAG);
		MapJS map = MapJS.of(nbt1 == null ? null : nbt1.get("KubeJS"));

		if (map == null)
		{
			map = new MapJS();
		}

		map.changeListener = m ->
		{
			CompoundNBT n = MapJS.nbt(m);

			if (n != null)
			{
				CompoundNBT n1 = entity.getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
				n1.put("KubeJS", n);
				entity.getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, n1);
			}
		};

		return map;
	}
}
