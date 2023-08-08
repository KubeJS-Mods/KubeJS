package dev.latvian.mods.kubejs.entity.forge;

import dev.latvian.mods.kubejs.entity.LivingEntityEventJS;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LivingEntityDropsEventJS extends LivingEntityEventJS {
	private final LivingDropsEvent event;
	public List<ItemEntity> eventDrops;

	public LivingEntityDropsEventJS(LivingDropsEvent e) {
		event = e;
	}

	@Override
	public LivingEntity getEntity() {
		return event.getEntity();
	}

	public DamageSource getSource() {
		return event.getSource();
	}

	public int getLootingLevel() {
		return event.getLootingLevel();
	}

	public boolean isRecentlyHit() {
		return event.isRecentlyHit();
	}

	public List<ItemEntity> getDrops() {
		if (eventDrops == null) {
			eventDrops = new ArrayList<>(event.getDrops());
		}

		return eventDrops;
	}

	@Nullable
	public ItemEntity addDrop(ItemStack stack) {
		if (!stack.isEmpty()) {
			var e = event.getEntity();
			var ei = new ItemEntity(e.level(), e.getX(), e.getY(), e.getZ(), stack);
			ei.setPickUpDelay(10);
			getDrops().add(ei);
			return ei;
		}

		return null;
	}

	@Nullable
	public ItemEntity addDrop(ItemStack stack, float chance) {
		if (chance >= 1F || event.getEntity().level().random.nextFloat() <= chance) {
			return addDrop(stack);
		}

		return null;
	}
}