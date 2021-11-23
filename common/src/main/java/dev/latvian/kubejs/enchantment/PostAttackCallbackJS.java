package dev.latvian.kubejs.enchantment;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class PostAttackCallbackJS {
	public final int level;
	public final WorldJS world;
	public final ServerJS server;
	public final LivingEntityJS entity;
	public final EntityJS target;
	public final EnchantmentJS enchantment;

    public PostAttackCallbackJS(LivingEntity entity, Entity target, int level, EnchantmentJS enchantment) {
        this.level = level;
		this.world = UtilsJS.getWorld(entity.level);
		this.server = world.getServer();
        this.entity = world.getLivingEntity(entity);
        this.target = world.getEntity(target);
		this.enchantment = enchantment;
    }

	public int getLevel() {
		return level;
	}

	public WorldJS getWorld() {
        return world;
    }

	public ServerJS getServer() {
        return server;
    }

    public LivingEntityJS getEntity() {
        return entity;
    }

    public EntityJS getTarget() {
        return target;
    }

	public EnchantmentJS getEnchantment() {
        return enchantment;
    }

	@Override
	public String toString() {
		return "PostAttackCallbackJS{" +
				"level=" + level +
				", world=" + world +
				", server=" + server +
				", entity=" + entity +
				", target=" + target +
				", enchantment=" + enchantment +
				'}';
	}
}
