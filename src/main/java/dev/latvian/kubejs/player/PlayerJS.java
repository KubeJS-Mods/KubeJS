package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author LatvianModder
 */
@DocClass
public abstract class PlayerJS<E extends EntityPlayer> extends LivingEntityJS
{
	public final transient E playerEntity;

	@DocField("Temporary data, mods can attach objects to this")
	public final Map<String, Object> data;

	private InventoryJS inventory;

	public PlayerJS(PlayerDataJS d, WorldJS w, E p)
	{
		super(w, p);
		data = d.data;
		playerEntity = p;
	}

	@Override
	public boolean isPlayer()
	{
		return true;
	}

	public boolean isFake()
	{
		return playerEntity instanceof FakePlayer;
	}

	public String toString()
	{
		return getName();
	}

	@DocMethod
	public InventoryJS getInventory()
	{
		if (inventory == null)
		{
			inventory = new InventoryJS(new InvWrapper(playerEntity.inventory));
		}

		return inventory;
	}

	public void give(Object item)
	{
		ItemHandlerHelper.giveItemToPlayer(playerEntity, ItemStackJS.of(item).getItemStack());
	}

	public void giveInHand(Object item)
	{
		ItemHandlerHelper.giveItemToPlayer(playerEntity, ItemStackJS.of(item).getItemStack(), getSelectedSlot());
	}

	public int getSelectedSlot()
	{
		return playerEntity.inventory.currentItem;
	}

	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
	{
		super.setPositionAndRotation(x, y, z, yaw, pitch);

		if (playerEntity instanceof EntityPlayerMP)
		{
			((EntityPlayerMP) playerEntity).connection.setPlayerLocation(x, y, z, yaw, pitch);
		}
	}

	@Override
	public void setStatusMessage(Object message)
	{
		playerEntity.sendStatusMessage(Text.of(message).component(), true);
	}

	@DocMethod
	public boolean isCreativeMode()
	{
		return playerEntity.capabilities.isCreativeMode;
	}

	@DocMethod
	public boolean isSpectator()
	{
		return playerEntity.isSpectator();
	}

	@DocMethod
	public abstract PlayerStatsJS getStats();

	@Override
	public void spawn()
	{
	}

	public void sendData(String channel, @Nullable Object data)
	{
		KubeJS.PROXY.sendData(playerEntity, channel, NBTBaseJS.of(data).asCompound().createNBT());
	}
}