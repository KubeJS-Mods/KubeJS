package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.text.Text;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class EntityJS implements MessageSender
{
	private final WorldJS world;

	@Ignore
	public final Entity entity;

	public EntityJS(WorldJS w, Entity e)
	{
		world = w;
		entity = e;
	}

	public WorldJS getWorld()
	{
		return world;
	}

	@Nullable
	public ServerJS getServer()
	{
		return getWorld().getServer();
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
	public void tell(@P("message") @T(Text.class) Object message)
	{
		entity.sendMessage(Text.of(message).component());
	}

	public String toString()
	{
		return getName() + "-" + getId();
	}

	@Nullable
	public ItemStackJS getItem()
	{
		return null;
	}

	public boolean isFrame()
	{
		return false;
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

	public void setFallDistance(@P("fallDistance") float fallDistance)
	{
		entity.fallDistance = fallDistance;
	}

	public float getStepHeight()
	{
		return entity.stepHeight;
	}

	public void setStepHeight(@P("stepHeight") float stepHeight)
	{
		entity.stepHeight = stepHeight;
	}

	public boolean getNoClip()
	{
		return entity.noClip;
	}

	public void setNoClip(@P("noClip") boolean noClip)
	{
		entity.noClip = noClip;
	}

	public boolean isSilent()
	{
		return entity.isSilent();
	}

	public void setSilent(@P("isSilent") boolean isSilent)
	{
		entity.setSilent(isSilent);
	}

	public boolean getNoGravity()
	{
		return entity.hasNoGravity();
	}

	public void setNoGravity(@P("noGravity") boolean noGravity)
	{
		entity.setNoGravity(noGravity);
	}

	public double getX()
	{
		return entity.posX;
	}

	public void setX(@P("x") double x)
	{
		entity.posX = x;
	}

	public double getY()
	{
		return entity.posY;
	}

	public void setY(@P("y") double y)
	{
		entity.posY = y;
	}

	public double getZ()
	{
		return entity.posZ;
	}

	public void setZ(@P("z") double z)
	{
		entity.posZ = z;
	}

	public float getYaw()
	{
		return entity.rotationYaw;
	}

	public void setYaw(@P("yaw") float yaw)
	{
		entity.rotationYaw = yaw;
	}

	public float getPitch()
	{
		return entity.rotationPitch;
	}

	public void setPitch(@P("pitch") float pitch)
	{
		entity.rotationPitch = pitch;
	}

	public int getTicksExisted()
	{
		return entity.ticksExisted;
	}

	public void setPosition(@P("block") BlockContainerJS block)
	{
		setPosition(block.getX() + 0.5D, block.getY() + 0.05D, block.getZ() + 0.5D);
	}

	public void setPosition(@P("x") double x, @P("y") double y, @P("z") double z)
	{
		setPositionAndRotation(x, y, z, getYaw(), getPitch());
	}

	public void setRotation(@P("yaw") float yaw, @P("pitch") float pitch)
	{
		setPositionAndRotation(getX(), getY(), getZ(), yaw, pitch);
	}

	public void setPositionAndRotation(@P("x") double x, @P("y") double y, @P("z") double z, @P("yaw") float yaw, @P("pitch") float pitch)
	{
		entity.setLocationAndAngles(x, y, z, yaw, pitch);
	}

	/*
	public void setDimensionPositionAndRotation(int dimension, double x, double y, double z, float yaw, float pitch)
	{
		setPositionAndRotation(x, y, z, yaw, pitch);
	}
	*/

	public void setMotion(@P("x") double x, @P("y") double y, @P("z") double z)
	{
		entity.motionX = x;
		entity.motionY = y;
		entity.motionZ = z;
	}

	public void addMotion(@P("x") double x, @P("y") double y, @P("z") double z)
	{
		setMotion(entity.motionX + x, entity.motionY + y, entity.motionZ + z);
	}

	@Override
	public int runCommand(@P("command") String command)
	{
		if (world instanceof ServerWorldJS)
		{
			return world.getServer().server.getCommandManager().executeCommand(entity, command);
		}

		return 0;
	}

	public void kill()
	{
		entity.onKillCommand();
	}

	public boolean startRiding(@P("entity") EntityJS e, @P("force") boolean force)
	{
		return entity.startRiding(e.entity, force);
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

	public boolean isPassenger(@P("entity") EntityJS e)
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

	public boolean isOnSameTeam(@P("entity") EntityJS e)
	{
		return entity.isOnSameTeam(e.entity);
	}

	public boolean isOnScoreboardTeam(@P("teamID") String teamID)
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

	public void setCustomNameAlwaysVisible(@P("flag") boolean b)
	{
		entity.setAlwaysRenderNameTag(b);
	}

	public boolean getCustomNameAlwaysVisible()
	{
		return entity.getAlwaysRenderNameTag();
	}

	public EnumFacing getHorizontalFacing()
	{
		return entity.getHorizontalFacing();
	}

	public float getEyeHeight()
	{
		return entity.getEyeHeight();
	}

	public BlockContainerJS getBlock()
	{
		return new BlockContainerJS(entity.world, entity.getPosition());
	}

	public void setOnFire(@P("seconds") int seconds)
	{
		entity.setFire(seconds);
	}

	public void extinguish()
	{
		entity.extinguish();
	}

	public NBTCompoundJS getFullNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		entity.writeToNBT(nbt);
		return NBTBaseJS.of(nbt).asCompound();
	}

	public void setFullNBT(@P("nbt") @T(NBTCompoundJS.class) Object n)
	{
		entity.readFromNBT(NBTBaseJS.of(n).asCompound().createNBT());
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

	public void setNbt(@P("nbt") NBTCompoundJS nbt)
	{
		NBTTagCompound n = nbt.createNBT();

		if (n != null)
		{
			entity.getEntityData().setTag("KubeJS", n);
		}
	}

	public NBTBaseJS getNBTData(@P("key") String key)
	{
		return getNbt().get(key);
	}

	public void setNBTData(@P("key") String key, @P("nbt") @Nullable Object nbt)
	{
		NBTCompoundJS n = getNbt();
		n.set(key, NBTBaseJS.of(nbt));
		setNbt(n);
	}

	public void playSound(@P("id") Object id, @P("volume") float volume, @P("pitch") float pitch)
	{
		SoundEvent event = id instanceof SoundEvent ? (SoundEvent) id : SoundEvent.REGISTRY.getObject(ID.of(id).mc());

		if (event != null)
		{
			entity.world.playSound(null, getX(), getY(), getZ(), event, entity.getSoundCategory(), volume, pitch);
		}
	}

	public void playSound(@P("id") Object id)
	{
		playSound(id, 1F, 1F);
	}

	public void spawn()
	{
		world.world.spawnEntity(entity);
	}
}