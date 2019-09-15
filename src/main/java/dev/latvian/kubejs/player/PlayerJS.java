package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.AttachedData;
import dev.latvian.kubejs.util.WithAttachedData;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public abstract class PlayerJS<E extends EntityPlayer> extends LivingEntityJS implements WithAttachedData
{
	private final E playerEntity;

	private final PlayerDataJS playerData;
	private InventoryJS inventory;

	public PlayerJS(PlayerDataJS d, WorldJS w, E p)
	{
		super(w, p);
		playerData = d;
		playerEntity = p;
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

	public E getPlayerEntity()
	{
		return playerEntity;
	}

	public boolean isFake()
	{
		return playerEntity instanceof FakePlayer;
	}

	public String toString()
	{
		return getName();
	}

	public InventoryJS getInventory()
	{
		if (inventory == null)
		{
			inventory = new InventoryJS(new InvWrapper(playerEntity.inventory));
		}

		return inventory;
	}

	public void give(@P("item") @T(ItemStackJS.class) Object item)
	{
		ItemHandlerHelper.giveItemToPlayer(playerEntity, ItemStackJS.of(item).getItemStack());
	}

	public void giveInHand(@P("item") @T(ItemStackJS.class) Object item)
	{
		ItemHandlerHelper.giveItemToPlayer(playerEntity, ItemStackJS.of(item).getItemStack(), getSelectedSlot());
	}

	public int getSelectedSlot()
	{
		return playerEntity.inventory.currentItem;
	}

	@Override
	public void setPositionAndRotation(@P("x") double x, @P("y") double y, @P("z") double z, @P("yaw") float yaw, @P("pitch") float pitch)
	{
		super.setPositionAndRotation(x, y, z, yaw, pitch);

		if (playerEntity instanceof EntityPlayerMP)
		{
			((EntityPlayerMP) playerEntity).connection.setPlayerLocation(x, y, z, yaw, pitch);
		}
	}

	@Override
	public void setStatusMessage(@P("message") Object message)
	{
		playerEntity.sendStatusMessage(Text.of(message).component(), true);
	}

	public boolean isCreativeMode()
	{
		return playerEntity.capabilities.isCreativeMode;
	}

	public boolean isSpectator()
	{
		return playerEntity.isSpectator();
	}

	public abstract PlayerStatsJS getStats();

	@Override
	public void spawn()
	{
	}

	public void sendData(@P("channel") String channel, @Nullable @P("data") Object data)
	{
		KubeJS.PROXY.sendData(playerEntity, channel, NBTBaseJS.of(data).asCompound().createNBT());
	}

	public void addFood(int food, float modifier)
	{
		playerEntity.getFoodStats().addStats(food, modifier);
	}
}