package dev.latvian.kubejs.player;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.Facing;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
@DocClass
public abstract class PlayerJS<E extends EntityPlayer> extends LivingEntityJS
{
	public final transient E entityPlayer;

	@DocField("Temporary data, mods can attach objects to this")
	public final Map<String, Object> data;

	private InventoryJS inventory;

	public PlayerJS(PlayerDataJS d, WorldJS w, E p)
	{
		super(w, p);
		data = d.data;
		entityPlayer = p;
	}

	@Override
	public boolean isPlayer()
	{
		return true;
	}

	@DocMethod
	public InventoryJS getInventory()
	{
		if (inventory == null)
		{
			inventory = new InventoryJS(new InvWrapper(entityPlayer.inventory));
		}

		return inventory;
	}

	public void give(Object item)
	{
		ItemHandlerHelper.giveItemToPlayer(entityPlayer, ItemStackJS.of(item).getItemStack());
	}

	public void giveInHand(Object item)
	{
		ItemHandlerHelper.giveItemToPlayer(entityPlayer, ItemStackJS.of(item).getItemStack(), getSelectedSlot());
	}

	public int getSelectedSlot()
	{
		return entityPlayer.inventory.currentItem;
	}

	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
	{
		super.setPositionAndRotation(x, y, z, yaw, pitch);

		if (entityPlayer instanceof EntityPlayerMP)
		{
			((EntityPlayerMP) entityPlayer).connection.setPlayerLocation(x, y, z, yaw, pitch);
		}
	}

	@Override
	public void setStatusMessage(Object message)
	{
		entityPlayer.sendStatusMessage(Text.of(message).component(), true);
	}

	@DocMethod
	public boolean isCreativeMode()
	{
		return entityPlayer.capabilities.isCreativeMode;
	}

	@DocMethod
	public boolean isSpectator()
	{
		return entityPlayer.isSpectator();
	}

	@DocMethod
	public abstract PlayerStatsJS getStats();

	@Override
	public void spawn()
	{
	}

	@Nullable
	public Map<String, Object> rayTrace(double distance)
	{
		Map<String, Object> map = new HashMap<>();
		RayTraceResult ray = ForgeHooks.rayTraceEyes(entityPlayer, distance);

		if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK)
		{
			map.put("block", new BlockContainerJS(world.world, ray.getBlockPos()));
			map.put("facing", Facing.VALUES[ray.sideHit.getIndex()]);
		}

		return map;
	}

	@Nullable
	public Map<String, Object> rayTrace()
	{
		return rayTrace(entityPlayer.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue());
	}
}