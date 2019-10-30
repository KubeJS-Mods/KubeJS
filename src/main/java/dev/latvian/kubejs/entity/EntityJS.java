package dev.latvian.kubejs.entity;

import com.mojang.authlib.GameProfile;
import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.documentation.Info;
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

	@MinecraftClass
	public final Entity minecraftEntity;

	public EntityJS(WorldJS w, Entity e)
	{
		world = w;
		minecraftEntity = e;
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
		return minecraftEntity.getUniqueID();
	}

	public ID getType()
	{
		return ID.of(EntityList.getKey(minecraftEntity));
	}

	@Override
	public String getName()
	{
		return minecraftEntity.getName();
	}

	@MinecraftClass
	public GameProfile getProfile()
	{
		return new GameProfile(getId(), getName());
	}

	@Override
	public Text getDisplayName()
	{
		return Text.of(minecraftEntity.getDisplayName());
	}

	@Override
	public void tell(@P("message") @T(Text.class) Object message)
	{
		minecraftEntity.sendMessage(Text.of(message).component());
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
		return minecraftEntity.getTags();
	}

	public boolean isAlive()
	{
		return minecraftEntity.isEntityAlive();
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
		return minecraftEntity.isSneaking();
	}

	public boolean isSprinting()
	{
		return minecraftEntity.isSprinting();
	}

	public boolean isGlowing()
	{
		return minecraftEntity.isGlowing();
	}

	public void setGlowing(boolean glowing)
	{
		minecraftEntity.setGlowing(glowing);
	}

	public boolean isInvisible()
	{
		return minecraftEntity.isInvisible();
	}

	public void setInvisible(boolean invisible)
	{
		minecraftEntity.setInvisible(invisible);
	}

	public boolean isBoss()
	{
		return !minecraftEntity.isNonBoss();
	}

	public boolean isMonster()
	{
		return minecraftEntity.isCreatureType(EnumCreatureType.MONSTER, false);
	}

	public boolean isAnimal()
	{
		return minecraftEntity.isCreatureType(EnumCreatureType.CREATURE, false);
	}

	public boolean isAmbientCreature()
	{
		return minecraftEntity.isCreatureType(EnumCreatureType.AMBIENT, false);
	}

	public boolean isWaterCreature()
	{
		return minecraftEntity.isCreatureType(EnumCreatureType.WATER_CREATURE, false);
	}

	public boolean isOnGround()
	{
		return minecraftEntity.onGround;
	}

	public float getFallDistance()
	{
		return minecraftEntity.fallDistance;
	}

	public void setFallDistance(@P("fallDistance") float fallDistance)
	{
		minecraftEntity.fallDistance = fallDistance;
	}

	public float getStepHeight()
	{
		return minecraftEntity.stepHeight;
	}

	public void setStepHeight(@P("stepHeight") float stepHeight)
	{
		minecraftEntity.stepHeight = stepHeight;
	}

	public boolean getNoClip()
	{
		return minecraftEntity.noClip;
	}

	public void setNoClip(@P("noClip") boolean noClip)
	{
		minecraftEntity.noClip = noClip;
	}

	public boolean isSilent()
	{
		return minecraftEntity.isSilent();
	}

	public void setSilent(@P("isSilent") boolean isSilent)
	{
		minecraftEntity.setSilent(isSilent);
	}

	public boolean getNoGravity()
	{
		return minecraftEntity.hasNoGravity();
	}

	public void setNoGravity(@P("noGravity") boolean noGravity)
	{
		minecraftEntity.setNoGravity(noGravity);
	}

	public double getX()
	{
		return minecraftEntity.posX;
	}

	public void setX(@P("x") double x)
	{
		minecraftEntity.posX = x;
	}

	public double getY()
	{
		return minecraftEntity.posY;
	}

	public void setY(@P("y") double y)
	{
		minecraftEntity.posY = y;
	}

	public double getZ()
	{
		return minecraftEntity.posZ;
	}

	public void setZ(@P("z") double z)
	{
		minecraftEntity.posZ = z;
	}

	public float getYaw()
	{
		return minecraftEntity.rotationYaw;
	}

	public void setYaw(@P("yaw") float yaw)
	{
		minecraftEntity.rotationYaw = yaw;
	}

	public float getPitch()
	{
		return minecraftEntity.rotationPitch;
	}

	public void setPitch(@P("pitch") float pitch)
	{
		minecraftEntity.rotationPitch = pitch;
	}

	public double getMotionX()
	{
		return minecraftEntity.motionX;
	}

	public void setMotionX(@P("x") double x)
	{
		minecraftEntity.motionX = x;
	}

	public double getMotionY()
	{
		return minecraftEntity.motionY;
	}

	public void setMotionY(@P("y") double y)
	{
		minecraftEntity.motionY = y;
	}

	public double getMotionZ()
	{
		return minecraftEntity.motionZ;
	}

	public void setMotionZ(@P("z") double z)
	{
		minecraftEntity.motionZ = z;
	}

	public int getTicksExisted()
	{
		return minecraftEntity.ticksExisted;
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
		minecraftEntity.setLocationAndAngles(x, y, z, yaw, pitch);
	}

	public void setMotion(@P("x") double x, @P("y") double y, @P("z") double z)
	{
		minecraftEntity.motionX = x;
		minecraftEntity.motionY = y;
		minecraftEntity.motionZ = z;
	}

	public void addMotion(@P("x") double x, @P("y") double y, @P("z") double z)
	{
		setMotion(minecraftEntity.motionX + x, minecraftEntity.motionY + y, minecraftEntity.motionZ + z);
	}

	@Override
	public int runCommand(@P("command") String command)
	{
		if (world instanceof ServerWorldJS)
		{
			return world.getServer().minecraftServer.getCommandManager().executeCommand(minecraftEntity, command);
		}

		return 0;
	}

	public void kill()
	{
		minecraftEntity.onKillCommand();
	}

	public boolean startRiding(@P("entity") EntityJS e, @P("force") boolean force)
	{
		return minecraftEntity.startRiding(e.minecraftEntity, force);
	}

	public void removePassengers()
	{
		minecraftEntity.removePassengers();
	}

	public void dismountRidingEntity()
	{
		minecraftEntity.dismountRidingEntity();
	}

	public EntityArrayList getPassengers()
	{
		return new EntityArrayList(world, minecraftEntity.getPassengers());
	}

	public boolean isPassenger(@P("entity") EntityJS e)
	{
		return minecraftEntity.isPassenger(e.minecraftEntity);
	}

	public EntityArrayList getRecursivePassengers()
	{
		return new EntityArrayList(world, minecraftEntity.getRecursivePassengers());
	}

	@Nullable
	public EntityJS getRidingEntity()
	{
		return world.getEntity(minecraftEntity.getRidingEntity());
	}

	@Info("Scoreboard team ID")
	public String getTeamID()
	{
		Team team = minecraftEntity.getTeam();
		return team == null ? "" : team.getName();
	}

	@Info("Checks if this entity is on the same scoreboard team as another entity")
	public boolean isOnSameTeam(@P("entity") EntityJS e)
	{
		return minecraftEntity.isOnSameTeam(e.minecraftEntity);
	}

	@Info("Checks if this entity is on scoreboard team")
	public boolean isOnScoreboardTeam(@P("teamID") String teamID)
	{
		Team team = minecraftEntity.getEntityWorld().getScoreboard().getTeam(teamID);
		return team != null && minecraftEntity.isOnScoreboardTeam(team);
	}

	public void setCustomName(String name)
	{
		minecraftEntity.setCustomNameTag(name);
	}

	@Info("Custom display name")
	public String getCustomName()
	{
		return minecraftEntity.getCustomNameTag();
	}

	@Info("Checks if custom display name is set")
	public boolean getHasCustomName()
	{
		return minecraftEntity.hasCustomName();
	}

	public void setCustomNameAlwaysVisible(@P("alwaysVisible") boolean b)
	{
		minecraftEntity.setAlwaysRenderNameTag(b);
	}

	@Info("Custom display name will always be visible above head")
	public boolean getCustomNameAlwaysVisible()
	{
		return minecraftEntity.getAlwaysRenderNameTag();
	}

	public EnumFacing getHorizontalFacing()
	{
		return minecraftEntity.getHorizontalFacing();
	}

	public EnumFacing getFacing()
	{
		if (getPitch() > 45F)
		{
			return EnumFacing.DOWN;
		}
		else if (getPitch() < -45F)
		{
			return EnumFacing.UP;
		}

		return getHorizontalFacing();
	}

	public float getEyeHeight()
	{
		return minecraftEntity.getEyeHeight();
	}

	@Info("Block position of the entity")
	public BlockContainerJS getBlock()
	{
		return new BlockContainerJS(minecraftEntity.world, minecraftEntity.getPosition());
	}

	@Info("Sets entity on fire for x seconds")
	public void setOnFire(@P("seconds") int seconds)
	{
		minecraftEntity.setFire(seconds);
	}

	public void extinguish()
	{
		minecraftEntity.extinguish();
	}

	@Info("Entity NBT")
	public NBTCompoundJS getFullNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		minecraftEntity.writeToNBT(nbt);
		return NBTBaseJS.of(nbt).asCompound();
	}

	public void setFullNBT(@P("nbt") @T(NBTCompoundJS.class) Object n)
	{
		minecraftEntity.readFromNBT(NBTBaseJS.of(n).asCompound().createNBT());
	}

	@Info("Custom NBT you can use for saving custom data")
	public NBTCompoundJS getNbt()
	{
		NBTTagCompound nbt = minecraftEntity.getEntityData();
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
			minecraftEntity.getEntityData().setTag("KubeJS", n);
		}
	}

	@Info("Get specific value from custom NBT")
	public NBTBaseJS getNBTData(@P("key") String key)
	{
		return getNbt().get(key);
	}

	@Info("Set specific value in custom NBT")
	public void setNBTData(@P("key") String key, @P("nbt") @Nullable Object nbt)
	{
		NBTCompoundJS n = getNbt();
		n.set(key, NBTBaseJS.of(nbt));
		setNbt(n);
	}

	@Info("Play sound at entity. Must be played from server side")
	public void playSound(@P("id") Object id, @P("volume") float volume, @P("pitch") float pitch)
	{
		SoundEvent event = id instanceof SoundEvent ? (SoundEvent) id : SoundEvent.REGISTRY.getObject(ID.of(id).mc());

		if (event != null)
		{
			minecraftEntity.world.playSound(null, getX(), getY(), getZ(), event, minecraftEntity.getSoundCategory(), volume, pitch);
		}
	}

	@Info("Play sound at entity. Must be played from server side")
	public void playSound(@P("id") Object id)
	{
		playSound(id, 1F, 1F);
	}

	@Info("Spawn entity in world")
	public void spawn()
	{
		world.minecraftWorld.spawnEntity(minecraftEntity);
	}
}