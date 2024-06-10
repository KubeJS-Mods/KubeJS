package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Info(value = """
	Modify dropped items and xp from block.
	""")
public class BlockDropsKubeEvent implements KubeEntityEvent {
	private final BlockDropsEvent event;

	public BlockDropsKubeEvent(BlockDropsEvent event) {
		this.event = event;
	}

	@Override
	public ServerLevel getLevel() {
		return event.getLevel();
	}

	@Override
	@Nullable
	public Entity getEntity() {
		return event.getBreaker();
	}

	@Info("The block that was broken.")
	public BlockContainerJS getBlock() {
		var block = new BlockContainerJS(event.getLevel(), event.getPos());
		block.cachedState = event.getState();
		block.cachedEntity = event.getBlockEntity();
		return block;
	}

	@Info("The experience dropped by the block.")
	public int getXp() {
		return event.getDroppedExperience();
	}

	@Info("Sets the experience dropped by the block.")
	public void setXp(int xp) {
		event.setDroppedExperience(xp);
	}

	@Info("Dropped item entities.")
	public List<ItemEntity> getItemEntities() {
		return event.getDrops();
	}

	@Info("Dropped items. Immutable.")
	public List<ItemStack> getItems() {
		return event.getDrops().stream().map(ItemEntity::getItem).toList();
	}

	public boolean containsItem(Ingredient item) {
		for (var drop : event.getDrops()) {
			if (item.test(drop.getItem())) {
				return true;
			}
		}

		return false;
	}

	public ItemEntity addItem(ItemStack item) {
		var entity = new ItemEntity(event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), item);
		event.getDrops().add(entity);
		return entity;
	}

	public void removeItem(Ingredient item) {
		event.getDrops().removeIf(drop -> item.test(drop.getItem()));
	}

	@Nullable
	@Info("The tool used when breaking this block. May be null.")
	public ItemStack getTool() {
		return event.getTool().isEmpty() ? null : event.getTool();
	}
}