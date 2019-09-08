package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.item.BoundItemStackJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.Facing;
import dev.latvian.kubejs.util.ID;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.SoundEvent;

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

	public UUID getId()
	{
		return entity.getUniqueID();
	}

	public ID getType()
	{
		return ID.of(EntityList.getKey(entity));
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
		return getName() + "-" + getId();
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

	public boolean getNoClip()
	{
		return entity.noClip;
	}

	public void setNoClip(boolean noClip)
	{
		entity.noClip = noClip;
	}

	public boolean isSilent()
	{
		return entity.isSilent();
	}

	public void setSilent(boolean isSilent)
	{
		entity.setSilent(isSilent);
	}

	public boolean getNoGravity()
	{
		return entity.hasNoGravity();
	}

	public void setNoGravity(boolean noGravity)
	{
		entity.setNoGravity(noGravity);
	}

	public double getX()
	{
		return entity.posX;
	}

	public void setX(double x)
	{
		entity.posX = x;
	}

	public double getY()
	{
		return entity.posY;
	}

	public void setY(double y)
	{
		entity.posY = y;
	}

	public double getZ()
	{
		return entity.posZ;
	}

	public void setZ(double z)
	{
		entity.posZ = z;
	}

	public float getYaw()
	{
		return entity.rotationYaw;
	}

	public void setYaw(float yaw)
	{
		entity.rotationYaw = yaw;
	}

	public float getPitch()
	{
		return entity.rotationPitch;
	}

	public void setPitch(float pitch)
	{
		entity.rotationPitch = pitch;
	}

	public int getTicksExisted()
	{
		return entity.ticksExisted;
	}

	public void setPosition(BlockContainerJS block)
	{
		setPosition(block.getX() + 0.5D, block.getY() + 0.05D, block.getZ() + 0.5D);
	}

	public void setPosition(double x, double y, double z)
	{
		setPositionAndRotation(x, y, z, getYaw(), getPitch());
	}

	public void setRotation(float yaw, float pitch)
	{
		setPositionAndRotation(getX(), getY(), getZ(), yaw, pitch);
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

	public boolean getHasCustomName()
	{
		return entity.hasCustomName();
	}

	public void setCustomNameAlwaysVisible(boolean b)
	{
		entity.setAlwaysRenderNameTag(b);
	}

	public boolean getCustomNameAlwaysVisible()
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

	public void extinguish()
	{
		entity.extinguish();
	}

	public NBTCompoundJS getNbt()
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

	public void setNbt(NBTCompoundJS nbt)
	{
		NBTTagCompound n = nbt.createNBT();

		if (n != null)
		{
			entity.getEntityData().setTag("KubeJS", n);
		}
	}

	public NBTBaseJS getNBTData(String key)
	{
		return getNbt().get(key);
	}

	public void setNBTData(String key, @Nullable Object nbt)
	{
		NBTCompoundJS n = getNbt();
		n.set(key, NBTBaseJS.of(nbt));
		setNbt(n);
	}

	public void playSound(Object id, float volume, float pitch)
	{
		SoundEvent event = id instanceof SoundEvent ? (SoundEvent) id : SoundEvent.REGISTRY.getObject(ID.of(id).mc());

		if (event != null)
		{
			entity.playSound(event, volume, pitch);
		}
	}

	public void spawn()
	{
		world.world.spawnEntity(entity);
	}
}