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
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class EntityJS implements MessageSender
{
	private static Map<String, DamageSource> damageSourceMap;

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

	public ResourceLocation getType()
	{
		return UtilsJS.getID(minecraftEntity.getType().getRegistryName());
	}

	@Override
	public Text getName()
	{
		return Text.of(minecraftEntity.getName());
	}

	@MinecraftClass
	public GameProfile getProfile()
	{
		return new GameProfile(getId(), minecraftEntity.getEntityString());
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
		return minecraftEntity.getName().getString() + "-" + getId();
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
		return minecraftEntity.isAlive();
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
		return !minecraftEntity.getType().getClassification().getPeacefulCreature();
	}

	public boolean isAnimal()
	{
		return minecraftEntity.getType().getClassification().getAnimal();
	}

	public boolean isAmbientCreature()
	{
		return minecraftEntity.getType().getClassification() == EntityClassification.AMBIENT;
	}

	public boolean isWaterCreature()
	{
		return minecraftEntity.getType().getClassification() == EntityClassification.WATER_CREATURE;
	}

	public boolean isPeacefulCreature()
	{
		return minecraftEntity.getType().getClassification().getPeacefulCreature();
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
		return minecraftEntity.getMotion().x;
	}

	public void setMotionX(@P("x") double x)
	{
		Vec3d m = minecraftEntity.getMotion();
		minecraftEntity.setMotion(x, m.y, m.z);
	}

	public double getMotionY()
	{
		return minecraftEntity.getMotion().y;
	}

	public void setMotionY(@P("y") double y)
	{
		Vec3d m = minecraftEntity.getMotion();
		minecraftEntity.setMotion(m.x, y, m.z);
	}

	public double getMotionZ()
	{
		return minecraftEntity.getMotion().z;
	}

	public void setMotionZ(@P("z") double z)
	{
		Vec3d m = minecraftEntity.getMotion();
		minecraftEntity.setMotion(m.x, m.y, z);
	}

	public void setMotion(@P("x") double x, @P("y") double y, @P("z") double z)
	{
		minecraftEntity.setMotion(x, y, z);
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

	public void addMotion(@P("x") double x, @P("y") double y, @P("z") double z)
	{
		minecraftEntity.setMotion(minecraftEntity.getMotion().add(x, y, z));
	}

	@Override
	public int runCommand(@P("command") String command)
	{
		if (world instanceof ServerWorldJS)
		{
			return world.getServer().minecraftServer.getCommandManager().handleCommand(minecraftEntity.getCommandSource(), command);
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
		minecraftEntity.stopRiding();
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

	public void setCustomName(Text name)
	{
		minecraftEntity.setCustomName(name.component());
	}

	@Info("Custom display name")
	public Text getCustomName()
	{
		return Text.of(minecraftEntity.getCustomName());
	}

	@Info("Checks if custom display name is set")
	public boolean getHasCustomName()
	{
		return minecraftEntity.hasCustomName();
	}

	public void setCustomNameAlwaysVisible(@P("alwaysVisible") boolean b)
	{
		minecraftEntity.setCustomNameVisible(b);
	}

	@Info("Custom display name will always be visible above head")
	public boolean getCustomNameAlwaysVisible()
	{
		return minecraftEntity.isCustomNameVisible();
	}

	public Direction getHorizontalFacing()
	{
		return minecraftEntity.getHorizontalFacing();
	}

	public Direction getFacing()
	{
		if (getPitch() > 45F)
		{
			return Direction.DOWN;
		}
		else if (getPitch() < -45F)
		{
			return Direction.UP;
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
		CompoundNBT nbt = new CompoundNBT();
		minecraftEntity.writeWithoutTypeId(nbt);
		return NBTBaseJS.of(nbt).asCompound();
	}

	public void setFullNBT(@P("nbt") @T(NBTCompoundJS.class) Object n)
	{
		minecraftEntity.read(NBTBaseJS.of(n).asCompound().createNBT());
	}

	@Info("Custom NBT you can use for saving custom data")
	public NBTCompoundJS getNbt()
	{
		CompoundNBT nbt = minecraftEntity.getPersistentData();
		CompoundNBT nbt1 = (CompoundNBT) nbt.get("KubeJS");

		if (nbt1 == null)
		{
			nbt1 = new CompoundNBT();
			nbt.put("KubeJS", nbt1);
		}

		return NBTBaseJS.of(nbt1).asCompound();
	}

	public void setNbt(@P("nbt") NBTCompoundJS nbt)
	{
		CompoundNBT n = nbt.createNBT();

		if (n != null)
		{
			minecraftEntity.getPersistentData().put("KubeJS", n);
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
		SoundEvent event = id instanceof SoundEvent ? (SoundEvent) id : ForgeRegistries.SOUND_EVENTS.getValue(UtilsJS.getID(id));

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
		world.minecraftWorld.addEntity(minecraftEntity);
	}

	public void attack(@P("source") String source, @P("hp") float hp)
	{
		if (damageSourceMap == null)
		{
			damageSourceMap = new HashMap<>();

			try
			{
				for (Field field : DamageSource.class.getDeclaredFields())
				{
					field.setAccessible(true);

					if (Modifier.isStatic(field.getModifiers()) && field.getType() == DamageSource.class)
					{
						DamageSource s = (DamageSource) field.get(null);
						damageSourceMap.put(s.getDamageType(), s);
					}
				}
			}
			catch (Exception ex)
			{
			}
		}

		DamageSource s = damageSourceMap.getOrDefault(source, DamageSource.GENERIC);
		minecraftEntity.attackEntityFrom(s, hp);
	}

	public void attack(@P("hp") float hp)
	{
		minecraftEntity.attackEntityFrom(DamageSource.GENERIC, hp);
	}

	public RayTraceResult rayTraceResult(double distance)
	{
		double f = minecraftEntity.rotationPitch;
		double f1 = minecraftEntity.rotationYaw;
		Vec3d vec3d = minecraftEntity.getEyePosition(1);
		double f2 = Math.cos(-f1 * (Math.PI / 180D) - (float) Math.PI);
		double f3 = Math.sin(-f1 * (Math.PI / 180D) - (float) Math.PI);
		double f4 = -Math.cos(-f * (Math.PI / 180D));
		double f5 = Math.sin(-f * (Math.PI / 180D));
		double f6 = f3 * f4;
		double f7 = f2 * f4;
		Vec3d vec3d1 = vec3d.add(f6 * distance, f5 * distance, f7 * distance);
		return minecraftEntity.world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, minecraftEntity));
	}

	@Nullable
	public Map<String, Object> rayTrace(@P("distance") double distance)
	{
		Map<String, Object> map = new HashMap<>();
		RayTraceResult ray = rayTraceResult(distance);

		if (ray.getType() != RayTraceResult.Type.MISS)
		{
			map.put("info", ray.hitInfo);
			map.put("hitX", ray.getHitVec().x);
			map.put("hitY", ray.getHitVec().y);
			map.put("hitZ", ray.getHitVec().z);

			if (ray instanceof BlockRayTraceResult)
			{
				map.put("block", new BlockContainerJS(getWorld().minecraftWorld, ((BlockRayTraceResult) ray).getPos()));
				map.put("facing", ((BlockRayTraceResult) ray).getFace());
				map.put("subHit", ray.subHit);
			}
			else if (ray instanceof EntityRayTraceResult)
			{
				map.put("entity", getWorld().getEntity(((EntityRayTraceResult) ray).getEntity()));
			}
		}

		return map;
	}
}