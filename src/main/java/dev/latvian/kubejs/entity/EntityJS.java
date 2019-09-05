package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.item.BoundItemStackJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.Facing;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class EntityJS implements MessageSender
{
	public final WorldJS world;
	public final transient Entity entity;

	public EntityJS(WorldJS w, Entity e)
	{
		world = w;
		entity = e;
	}

	public boolean isServer()
	{
		return !entity.world.isRemote;
	}

	public UUID getID()
	{
		return entity.getUniqueID();
	}

	@Override
	public String getName()
	{
		return entity.getName();
	}

	@Override
	public Text getDisplayName()
	{
		return Text.of(entity.getDisplayName());
	}

	@Override
	public void tell(Object message)
	{
		entity.sendMessage(Text.of(message).component());
	}

	public String toString()
	{
		return getName() + "-" + getID();
	}

	public ItemStackJS asItem()
	{
		if (entity instanceof EntityItem)
		{
			return new BoundItemStackJS(((EntityItem) entity).getItem());
		}

		return EmptyItemStackJS.INSTANCE;
	}

	public Set<String> getTags()
	{
		return entity.getTags();
	}

	public boolean isAlive()
	{
		return entity.isEntityAlive();
	}

	public boolean isLiving()
	{
		return false;
	}

	public boolean isPlayer()
	{
		return false;
	}

	public boolean isSneaking()
	{
		return entity.isSneaking();
	}

	public boolean isSprinting()
	{
		return entity.isSprinting();
	}

	public boolean isGlowing()
	{
		return entity.isGlowing();
	}

	public void setGlowing(boolean glowing)
	{
		entity.setGlowing(glowing);
	}

	public boolean isInvisible()
	{
		return entity.isInvisible();
	}

	public void setInvisible(boolean invisible)
	{
		entity.setInvisible(invisible);
	}

	public boolean isBoss()
	{
		return !entity.isNonBoss();
	}

	public boolean isMonster()
	{
		return entity.isCreatureType(EnumCreatureType.MONSTER, false);
	}

	public boolean isAnimal()
	{
		return entity.isCreatureType(EnumCreatureType.CREATURE, false);
	}

	public boolean isAmbientCreature()
	{
		return entity.isCreatureType(EnumCreatureType.AMBIENT, false);
	}

	public boolean isWaterCreature()
	{
		return entity.isCreatureType(EnumCreatureType.WATER_CREATURE, false);
	}

	public boolean isOnGround()
	{
		return entity.onGround;
	}

	public float getFallDistance()
	{
		return entity.fallDistance;
	}

	public void setFallDistance(float fallDistance)
	{
		entity.fallDistance = fallDistance;
	}

	public float getStepHeight()
	{
		return entity.stepHeight;
	}

	public void setStepHeight(float stepHeight)
	{
		entity.stepHeight = stepHeight;
	}

	public boolean isNoClip()
	{
		return entity.noClip;
	}

	public void setNoClip(boolean noClip)
	{
		entity.noClip = noClip;
	}

	public double x()
	{
		return entity.posX;
	}

	public double y()
	{
		return entity.posY;
	}

	public double z()
	{
		return entity.posZ;
	}

	public float getYaw()
	{
		return entity.rotationYaw;
	}

	public float getPitch()
	{
		return entity.rotationPitch;
	}

	public int getTicksExisted()
	{
		return entity.ticksExisted;
	}

	public void setPosition(double x, double y, double z)
	{
		setPositionAndRotation(x, y, z, getYaw(), getPitch());
	}

	public void setRotation(float yaw, float pitch)
	{
		setPositionAndRotation(x(), y(), z(), yaw, pitch);
	}

	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
	{
		entity.setLocationAndAngles(x, y, z, yaw, pitch);
	}

	/*
	public void setDimensionPositionAndRotation(int dimension, double x, double y, double z, float yaw, float pitch)
	{
		setPositionAndRotation(x, y, z, yaw, pitch);
	}
	*/

	public void setMotion(double x, double y, double z)
	{
		entity.motionX = x;
		entity.motionY = y;
		entity.motionZ = z;
	}

	public void addMotion(double x, double y, double z)
	{
		setMotion(entity.motionX + x, entity.motionY + y, entity.motionZ + z);
	}

	@Override
	public int runCommand(String command)
	{
		if (world instanceof ServerWorldJS)
		{
			return ((ServerWorldJS) world).server.server.getCommandManager().executeCommand(entity, command);
		}

		return 0;
	}

	public void kill()
	{
		entity.onKillCommand();
	}

	public boolean startRiding(EntityJS entity, boolean force)
	{
		return entity.startRiding(entity, force);
	}

	public void removePassengers()
	{
		entity.removePassengers();
	}

	public void dismountRidingEntity()
	{
		entity.dismountRidingEntity();
	}

	public EntityArrayList getPassengers()
	{
		return new EntityArrayList(world, entity.getPassengers());
	}

	public boolean isPassenger(EntityJS e)
	{
		return entity.isPassenger(e.entity);
	}

	public EntityArrayList getRecursivePassengers()
	{
		return new EntityArrayList(world, entity.getRecursivePassengers());
	}

	@Nullable
	public EntityJS getRidingEntity()
	{
		return world.getEntity(entity.getRidingEntity());
	}

	public String getTeamID()
	{
		Team team = entity.getTeam();
		return team == null ? "" : team.getName();
	}

	public boolean isOnSameTeam(EntityJS e)
	{
		return entity.isOnSameTeam(e.entity);
	}

	public boolean isOnScoreboardTeam(String teamID)
	{
		Team team = entity.getEntityWorld().getScoreboard().getTeam(teamID);
		return team != null && entity.isOnScoreboardTeam(team);
	}

	public void setCustomName(String name)
	{
		entity.setCustomNameTag(name);
	}

	public String getCustomName()
	{
		return entity.getCustomNameTag();
	}

	public boolean hasCustomName()
	{
		return entity.hasCustomName();
	}

	public void setAlwaysRenderName(boolean alwaysRenderName)
	{
		entity.setAlwaysRenderNameTag(alwaysRenderName);
	}

	public boolean getAlwaysRenderName()
	{
		return entity.getAlwaysRenderNameTag();
	}

	public Facing getHorizontalFacing()
	{
		return Facing.VALUES[entity.getHorizontalFacing().getIndex()];
	}

	public float getEyeHeight()
	{
		return entity.getEyeHeight();
	}

	public BlockContainerJS getBlock()
	{
		return new BlockContainerJS(entity.world, entity.getPosition());
	}

	public void setOnFire(int seconds)
	{
		entity.setFire(seconds);
	}

	public NBTCompoundJS getNBT()
	{
		NBTTagCompound nbt = entity.getEntityData();
		NBTTagCompound nbt1 = (NBTTagCompound) nbt.getTag("KubeJS");

		if (nbt1 == null)
		{
			nbt1 = new NBTTagCompound();
			nbt.setTag("KubeJS", nbt1);
		}

		return NBTBaseJS.of(nbt1).asCompound();
	}

	public void setNBT(NBTCompoundJS nbt)
	{
		NBTTagCompound n = nbt.createNBT();

		if (n != null)
		{
			entity.getEntityData().setTag("KubeJS", n);
		}
	}
}