package dev.latvian.kubejs.player;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.Map;

/**
 * @author LatvianModder
 */
@DocClass
public abstract class PlayerJS<E extends EntityPlayer> extends LivingEntityJS
{
	public final transient E player;

	@DocField("Temporary data, mods can attach objects to this")
	public final Map<String, Object> data;

	private InventoryJS inventory;

	public PlayerJS(PlayerDataJS d, WorldJS w, E p)
	{
		super(w, p);
		data = d.data;
		player = p;
	}

	@Override
	public boolean isPlayer()
	{
		return true;
	}

	@DocMethod
	public InventoryJS inventory()
	{
		if (inventory == null)
		{
			inventory = new InventoryJS(new InvWrapper(player.inventory));
		}

		return inventory;
	}

	public void give(Object item)
	{
		ItemHandlerHelper.giveItemToPlayer(player, ItemStackJS.of(item).itemStack());
	}

	public void giveInHand(Object item)
	{
		ItemHandlerHelper.giveItemToPlayer(player, ItemStackJS.of(item).itemStack(), getSelectedSlot());
	}

	public int getSelectedSlot()
	{
		return player.inventory.currentItem;
	}

	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
	{
		super.setPositionAndRotation(x, y, z, yaw, pitch);

		if (player instanceof EntityPlayerMP)
		{
			((EntityPlayerMP) player).connection.setPlayerLocation(x, y, z, yaw, pitch);
		}
	}

	@Override
	public void setStatusMessage(Object message)
	{
		player.sendStatusMessage(Text.of(message).component(), true);
	}

	@DocMethod
	public boolean isCreativeMode()
	{
		return player.capabilities.isCreativeMode;
	}

	@DocMethod
	public boolean isSpectator()
	{
		return player.isSpectator();
	}

	@DocMethod
	public abstract PlayerStatsJS stats();
}