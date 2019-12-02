package dev.latvian.kubejs.player;

import com.mojang.authlib.GameProfile;
import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.AttachedData;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.util.WithAttachedData;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public abstract class PlayerJS<E extends PlayerEntity> extends LivingEntityJS implements WithAttachedData
{
	@MinecraftClass
	public final E minecraftPlayer;

	private final PlayerDataJS playerData;
	private InventoryJS inventory;

	public PlayerJS(PlayerDataJS d, WorldJS w, E p)
	{
		super(w, p);
		playerData = d;
		minecraftPlayer = p;
	}

	@Override
	public AttachedData getData()
	{
		return playerData.getData();
	}

	@Override
	public boolean isPlayer()
	{
		return true;
	}

	public boolean isFake()
	{
		return minecraftPlayer instanceof FakePlayer;
	}

	public String toString()
	{
		return minecraftPlayer.getGameProfile().getName();
	}

	@Override
	public GameProfile getProfile()
	{
		return minecraftPlayer.getGameProfile();
	}

	public InventoryJS getInventory()
	{
		if (inventory == null)
		{
			inventory = new InventoryJS(minecraftPlayer.inventory)
			{
				@Override
				public void markDirty()
				{
					sendInventoryUpdate();
				}
			};
		}

		return inventory;
	}

	public void sendInventoryUpdate()
	{
		minecraftPlayer.inventory.markDirty();
		minecraftPlayer.container.detectAndSendChanges();
	}

	public void give(@P("item") @T(ItemStackJS.class) Object item)
	{
		ItemHandlerHelper.giveItemToPlayer(minecraftPlayer, ItemStackJS.of(item).getItemStack());
	}

	public void giveInHand(@P("item") @T(ItemStackJS.class) Object item)
	{
		ItemHandlerHelper.giveItemToPlayer(minecraftPlayer, ItemStackJS.of(item).getItemStack(), getSelectedSlot());
	}

	public int getSelectedSlot()
	{
		return minecraftPlayer.inventory.currentItem;
	}

	public void setSelectedSlot(@P("index") int index)
	{
		minecraftPlayer.inventory.currentItem = MathHelper.clamp(index, 0, 8);
	}

	public ItemStackJS getMouseItem()
	{
		return ItemStackJS.of(minecraftPlayer.inventory.getItemStack());
	}

	public void setMouseItem(@P("item") @T(ItemStackJS.class) Object item)
	{
		minecraftPlayer.inventory.setItemStack(ItemStackJS.of(item).getItemStack());
	}

	@Override
	public void setPositionAndRotation(@P("x") double x, @P("y") double y, @P("z") double z, @P("yaw") float yaw, @P("pitch") float pitch)
	{
		super.setPositionAndRotation(x, y, z, yaw, pitch);

		if (minecraftPlayer instanceof ServerPlayerEntity)
		{
			((ServerPlayerEntity) minecraftPlayer).connection.setPlayerLocation(x, y, z, yaw, pitch);
		}
	}

	@Override
	public void setStatusMessage(@P("message") Object message)
	{
		minecraftPlayer.sendStatusMessage(Text.of(message).component(), true);
	}

	public boolean isCreativeMode()
	{
		return minecraftPlayer.isCreative();
	}

	public boolean isSpectator()
	{
		return minecraftPlayer.isSpectator();
	}

	public abstract PlayerStatsJS getStats();

	@Override
	public void spawn()
	{
	}

	@Override
	public MapJS getNbt()
	{
		CompoundNBT nbt = minecraftEntity.getPersistentData();
		CompoundNBT nbt1 = (CompoundNBT) nbt.get(PlayerEntity.PERSISTED_NBT_TAG);
		MapJS map = MapJS.of(nbt1 == null ? null : nbt1.get("KubeJS"));

		if (map == null)
		{
			map = new MapJS();
		}

		map.changeListener = m -> {
			CompoundNBT n = MapJS.nbt(m);

			if (n != null)
			{
				CompoundNBT n1 = minecraftEntity.getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
				n1.put("KubeJS", n);
				minecraftEntity.getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, n1);
			}
		};

		return map;
	}

	public void sendData(@P("channel") String channel, @Nullable @P("data") Object data)
	{
	}

	public void addFood(@P("food") int f, @P("modifier") float m)
	{
		minecraftPlayer.getFoodStats().addStats(f, m);
	}

	public int getFoodLevel()
	{
		return minecraftPlayer.getFoodStats().getFoodLevel();
	}

	public void setFoodLevel(int foodLevel)
	{
		minecraftPlayer.getFoodStats().setFoodLevel(foodLevel);
	}

	public void addExhaustion(float exhaustion)
	{
		minecraftPlayer.addExhaustion(exhaustion);
	}

	public void addXP(@P("xp") int xp)
	{
		//FIXME: minecraftPlayer.addExperience(xp);
	}

	public void addXPLevels(@P("levels") int l)
	{
		minecraftPlayer.addExperienceLevel(l);
	}

	public void setXp(@P("xp") int xp)
	{
		//minecraftPlayer.experienceTotal = 0;
		//minecraftPlayer.experience = 0F;
		//minecraftPlayer.experienceLevel = 0;
		//FIXME: minecraftPlayer.addExperience(xp);
	}

	public int getXp()
	{
		return minecraftPlayer.experienceTotal;
	}

	public void setXpLevel(@P("level") int l)
	{
		minecraftPlayer.experienceTotal = 0;
		minecraftPlayer.experience = 0F;
		minecraftPlayer.experienceLevel = 0;
		minecraftPlayer.addExperienceLevel(l);
	}

	public int getXpLevel()
	{
		return minecraftPlayer.experienceLevel;
	}

	public abstract void openOverlay(Overlay overlay);

	public abstract void closeOverlay(String overlay);

	public void closeOverlay(Overlay overlay)
	{
		closeOverlay(overlay.id);
	}

	public void boostElytraFlight()
	{
		if (minecraftPlayer.isElytraFlying())
		{
			Vec3d v = minecraftPlayer.getLookVec();
			double d0 = 1.5D;
			double d1 = 0.1D;
			Vec3d m = minecraftPlayer.getMotion();
			minecraftPlayer.setMotion(m.add(v.x * 0.1D + (v.x * 1.5D - m.x) * 0.5D, v.y * 0.1D + (v.y * 1.5D - m.y) * 0.5D, v.z * 0.1D + (v.z * 1.5D - m.z) * 0.5D));
		}
	}

	public void closeInventory()
	{
		minecraftPlayer.closeScreen();
	}

	@MinecraftClass
	public Container getOpenInventory()
	{
		return minecraftPlayer.openContainer;
	}

	public abstract boolean isMiningBlock();
}