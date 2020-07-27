package dev.latvian.kubejs.entity;

import com.mojang.authlib.GameProfile;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.WrappedJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
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
public class EntityJS implements MessageSender, WrappedJS
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

	@ID
	public String getType()
	{
		return minecraftEntity.getType().getRegistryName().toString();
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
	public void tell(Object message)
	{
		minecraftEntity.sendMessage(Text.of(message).component(), Util.DUMMY_UUID);
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

	public boolean isCrouching()
	{
		return minecraftEntity.isCrouching();
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
		return minecraftEntity.isOnGround();
	}

	public float getFallDistance()
	{
		return minecraftEntity.fallDistance;
	}

	public void setFallDistance(float fallDistance)
	{
		minecraftEntity.fallDistance = fallDistance;
	}

	public float getStepHeight()
	{
		return minecraftEntity.stepHeight;
	}

	public void setStepHeight(float stepHeight)
	{
		minecraftEntity.stepHeight = stepHeight;
	}

	public boolean getNoClip()
	{
		return minecraftEntity.noClip;
	}

	public void setNoClip(boolean noClip)
	{
		minecraftEntity.noClip = noClip;
	}

	public boolean isSilent()
	{
		return minecraftEntity.isSilent();
	}

	public void setSilent(boolean isSilent)
	{
		minecraftEntity.setSilent(isSilent);
	}

	public boolean getNoGravity()
	{
		return minecraftEntity.hasNoGravity();
	}

	public void setNoGravity(boolean noGravity)
	{
		minecraftEntity.setNoGravity(noGravity);
	}

	public double getX()
	{
		return minecraftEntity.getPosX();
	}

	public void setX(double x)
	{
		minecraftEntity.setPosition(x, getY(), getZ());
	}

	public double getY()
	{
		return minecraftEntity.getPosY();
	}

	public void setY(double y)
	{
		minecraftEntity.setPosition(getX(), y, getZ());
	}

	public double getZ()
	{
		return minecraftEntity.getPosZ();
	}

	public void setZ(double z)
	{
		minecraftEntity.setPosition(getX(), getY(), z);
	}

	public float getYaw()
	{
		return minecraftEntity.rotationYaw;
	}

	public void setYaw(float yaw)
	{
		minecraftEntity.rotationYaw = yaw;
	}

	public float getPitch()
	{
		return minecraftEntity.rotationPitch;
	}

	public void setPitch(float pitch)
	{
		minecraftEntity.rotationPitch = pitch;
	}

	public double getMotionX()
	{
		return minecraftEntity.getMotion().x;
	}

	public void setMotionX(double x)
	{
		Vector3d m = minecraftEntity.getMotion();
		minecraftEntity.setMotion(x, m.y, m.z);
	}

	public double getMotionY()
	{
		return minecraftEntity.getMotion().y;
	}

	public void setMotionY(double y)
	{
		Vector3d m = minecraftEntity.getMotion();
		minecraftEntity.setMotion(m.x, y, m.z);
	}

	public double getMotionZ()
	{
		return minecraftEntity.getMotion().z;
	}

	public void setMotionZ(double z)
	{
		Vector3d m = minecraftEntity.getMotion();
		minecraftEntity.setMotion(m.x, m.y, z);
	}

	public void setMotion(double x, double y, double z)
	{
		minecraftEntity.setMotion(x, y, z);
	}

	public int getTicksExisted()
	{
		return minecraftEntity.ticksExisted;
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
		minecraftEntity.setLocationAndAngles(x, y, z, yaw, pitch);
	}

	public void addMotion(double x, double y, double z)
	{
		minecraftEntity.setMotion(minecraftEntity.getMotion().add(x, y, z));
	}

	@Override
	public int runCommand(String command)
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

	public boolean startRiding(EntityJS e, boolean force)
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

	public boolean isPassenger(EntityJS e)
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

	public String getTeamId()
	{
		Team team = minecraftEntity.getTeam();
		return team == null ? "" : team.getName();
	}

	public boolean isOnSameTeam(EntityJS e)
	{
		return minecraftEntity.isOnSameTeam(e.minecraftEntity);
	}

	public boolean isOnScoreboardTeam(String teamID)
	{
		Team team = minecraftEntity.getEntityWorld().getScoreboard().getTeam(teamID);
		return team != null && minecraftEntity.isOnScoreboardTeam(team);
	}

	public void setCustomName(Text name)
	{
		minecraftEntity.setCustomName(name.component());
	}

	public Text getCustomName()
	{
		return Text.of(minecraftEntity.getCustomName());
	}

	public boolean getHasCustomName()
	{
		return minecraftEntity.hasCustomName();
	}

	public void setCustomNameAlwaysVisible(boolean b)
	{
		minecraftEntity.setCustomNameVisible(b);
	}

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

	public BlockContainerJS getBlock()
	{
		return new BlockContainerJS(minecraftEntity.world, minecraftEntity.getPosition());
	}

	public void setOnFire(int seconds)
	{
		minecraftEntity.setFire(seconds);
	}

	public void extinguish()
	{
		minecraftEntity.extinguish();
	}

	public MapJS getFullNBT()
	{
		CompoundNBT nbt = new CompoundNBT();
		minecraftEntity.writeWithoutTypeId(nbt);
		return MapJS.of(nbt);
	}

	public void setFullNBT(Object n)
	{
		CompoundNBT nbt = MapJS.nbt(n);

		if (nbt != null)
		{
			minecraftEntity.read(nbt);
		}
	}

	public MapJS getNbt()
	{
		CompoundNBT nbt = minecraftEntity.getPersistentData();
		MapJS map = MapJS.of(nbt.get("KubeJS"));

		if (map == null)
		{
			map = new MapJS();
		}

		map.changeListener = o -> {
			CompoundNBT n = MapJS.nbt(o);

			if (n != null)
			{
				minecraftEntity.getPersistentData().put("KubeJS", n);
			}
		};

		return map;
	}

	public void playSound(@ID String id, float volume, float pitch)
	{
		SoundEvent event = ForgeRegistries.SOUND_EVENTS.getValue(UtilsJS.getMCID(id));

		if (event != null)
		{
			minecraftEntity.world.playSound(null, getX(), getY(), getZ(), event, minecraftEntity.getSoundCategory(), volume, pitch);
		}
	}

	public void playSound(@ID String id)
	{
		playSound(id, 1F, 1F);
	}

	public void spawn()
	{
		world.minecraftWorld.addEntity(minecraftEntity);
	}

	public void attack(String source, float hp)
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

	public void attack(float hp)
	{
		minecraftEntity.attackEntityFrom(DamageSource.GENERIC, hp);
	}

	public RayTraceResult rayTraceResult(double distance)
	{
		double f = minecraftEntity.rotationPitch;
		double f1 = minecraftEntity.rotationYaw;
		Vector3d vec3d = minecraftEntity.getEyePosition(1);
		double f2 = Math.cos(-f1 * (Math.PI / 180D) - (float) Math.PI);
		double f3 = Math.sin(-f1 * (Math.PI / 180D) - (float) Math.PI);
		double f4 = -Math.cos(-f * (Math.PI / 180D));
		double f5 = Math.sin(-f * (Math.PI / 180D));
		double f6 = f3 * f4;
		double f7 = f2 * f4;
		Vector3d vec3d1 = vec3d.add(f6 * distance, f5 * distance, f7 * distance);
		return minecraftEntity.world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, minecraftEntity));
	}

	@Nullable
	public Map<String, Object> rayTrace(double distance)
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