package dev.latvian.mods.kubejs.entity.forge;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.entity.ItemEntityJS;
import dev.latvian.mods.kubejs.entity.LivingEntityEventJS;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class LivingEntityDropsEventJS extends LivingEntityEventJS {
	public static final EventHandler EVENT = EventHandler.server(LivingEntityDropsEventJS.class).name("entityDrops").cancelable().legacy("entity.drops");

	private final LivingDropsEvent event;
	public List<ItemEntityJS> eventDrops;

	public LivingEntityDropsEventJS(LivingDropsEvent e) {
		event = e;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(event.getEntity());
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

	public List<ItemEntityJS> getDrops() {
		if (eventDrops == null) {
			eventDrops = new ArrayList<>();

			for (var entity : event.getDrops()) {
				eventDrops.add(new ItemEntityJS(entity));
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
			var ie = new ItemEntityJS(ei);
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