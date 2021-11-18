package dev.latvian.kubejs.enchantment;

import dev.latvian.kubejs.entity.DamageSourceJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.world.damagesource.DamageSource;

public class DamageProtectionCallbackJS {
	public final int level;
	public final WorldJS world;
	public final DamageSourceJS source;
	public final EnchantmentJS enchantment;

    public DamageProtectionCallbackJS(int level, DamageSource source, EnchantmentJS enchantment) {
        this.level = level;
		if(source.getDirectEntity() != null) {
			this.world = UtilsJS.getWorld(source.getDirectEntity().level);
		}else{
			// Can cause Null Pointer Exception if both direct entity and source entity are null.
			this.world = UtilsJS.getWorld(source.getEntity().level);
		}
        this.source = new DamageSourceJS(world,source);
        this.enchantment = enchantment;
    }

	public int getLevel() {
        return level;
    }

    public WorldJS getWorld() {
        return world;
    }

    public DamageSourceJS getSource() {
        return source;
    }

    public EnchantmentJS getEnchantment() {
        return enchantment;
    }
}
