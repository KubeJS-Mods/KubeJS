package dev.latvian.mods.kubejs.entity.forge;

import dev.latvian.mods.kubejs.entity.DamageSourceJS;
import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.entity.ItemEntityJS;
import dev.latvian.mods.kubejs.entity.LivingEntityEventJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class LivingEntityDropsEventJS extends LivingEntityEventJS {
	private final LivingDropsEvent event;
	public List<ItemEntityJS> eventDrops;

	public LivingEntityDropsEventJS(LivingDropsEvent e) {
		event = e;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(event.getEntity());
	}

	public DamageSourceJS getSource() {
		return new DamageSourceJS(getLevel(), event.getSource());
	}

	public int getLootingLevel() {
		return event.getLootingLevel();
	}

	public boolean isRecentlyHit() {
		return event.isRecentlyHit();
	}

	public List<ItemEntityJS> getDrops() {
		if (eventDrops == null) {
			eventDrops = new ArrayList<>();

			for (var entity : event.getDrops()) {
				eventDrops.add(new ItemEntityJS(getLevel(), entity));
			}
		}

		return eventDrops;
	}

	@Nullable
	public ItemEntityJS addDrop(Object item) {
        var i = ItemStackJS.of(item).getItemStack();

		if (!i.isEmpty()) {
            var e = event.getEntity();
            var ei = new ItemEntity(e.level, e.getX(), e.getY(), e.getZ(), i);
			ei.setPickUpDelay(10);
            var ie = new ItemEntityJS(getLevel(), ei);
			getDrops().add(ie);
			return ie;
		}

		return null;
	}

	@Nullable
	public ItemEntityJS addDrop(Object item, float chance) {
		if (chance >= 1F || event.getEntity().level.random.nextFloat() <= chance) {
			return addDrop(item);
		}

		return null;
	}
}