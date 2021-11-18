package dev.latvian.kubejs.enchantment;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class PostAttackCallbackJS {
	public final int level;
	public final WorldJS world;
	public final LivingEntityJS entity;
	public final EntityJS target;
	public final EnchantmentJS enchantment;

    public PostAttackCallbackJS(int level, LivingEntity entity, Entity target, EnchantmentJS enchantment) {
        this.level = level;
		this.world = UtilsJS.getWorld(entity.level);
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

    public LivingEntityJS getEntity() {
        return entity;
    }

    public EntityJS getTarget() {
        return target;
    }

	public EnchantmentJS getEnchantment() {
        return enchantment;
    }
}
