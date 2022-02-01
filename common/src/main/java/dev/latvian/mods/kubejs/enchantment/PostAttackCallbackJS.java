package dev.latvian.mods.kubejs.enchantment;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.entity.LivingEntityJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class PostAttackCallbackJS {
	public final int enchantLevel;
	public final LevelJS level;
	public final ServerJS server;
	public final LivingEntityJS entity;
	public final EntityJS target;
	public final EnchantmentJS enchantment;

    public PostAttackCallbackJS(LivingEntity entity, Entity target, int enchantLevel, EnchantmentJS enchantment) {
        this.enchantLevel = enchantLevel;
		this.level = UtilsJS.getLevel(entity.level);
		this.server = level.getServer();
        this.entity = level.getLivingEntity(entity);
        this.target = level.getEntity(target);
		this.enchantment = enchantment;
    }

	public int getEnchantLevel() {
		return enchantLevel;
	}

	public LevelJS getLevel() {
        return level;
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
				"level=" + enchantLevel +
				", world=" + level +
				", server=" + server +
				", entity=" + entity +
				", target=" + target +
				", enchantment=" + enchantment +
				'}';
	}
}
