package dev.latvian.mods.kubejs.client.highlight;

import dev.latvian.mods.kubejs.client.ClientPlayerKubeEvent;
import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

@Info("""
	Invoked when block and entity highlight is rendered.
	""")
public class HighlightKubeEvent extends ClientPlayerKubeEvent {
	private final Minecraft mc;
	private final HighlightRenderer renderer;

	public HighlightKubeEvent(Minecraft mc, HighlightRenderer renderer) {
		super(mc.player);
		this.mc = mc;
		this.renderer = renderer;
	}

	@Override
	public Minecraft getClient() {
		return mc;
	}

	private int colorToInt(KubeColor color) {
		int c = color.kjs$getRGB();
		return c == 0 ? 0x000001 : c;
	}

	public void addBlock(BlockPos pos, KubeColor color) {
		if (!getPlayer().level().getBlockState(pos).isAir()) {
			int col = colorToInt(color);
			renderer.highlightedBlocks.put(pos.asLong(), col);
			renderer.uniqueColors.add(col);
		}
	}

	public void addBlocks(BlockPos from, BlockPos to, KubeColor color) {
		int col = colorToInt(color);
		boolean added = false;

		int minX = Math.min(from.getX(), to.getX());
		int minY = Math.min(from.getY(), to.getY());
		int minZ = Math.min(from.getZ(), to.getZ());
		int maxX = Math.max(from.getX(), to.getX());
		int maxY = Math.max(from.getY(), to.getY());
		int maxZ = Math.max(from.getZ(), to.getZ());
		var level = getPlayer().level();

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					var pos = new BlockPos(x, y, z);

					if (!level.getBlockState(pos).isAir()) {
						renderer.highlightedBlocks.put(pos.asLong(), col);
						added = true;
					}
				}
			}
		}

		if (added) {
			renderer.uniqueColors.add(col);
		}
	}

	public void addTargetBlock(KubeColor color) {
		if (mc.hitResult instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK) {
			addBlock(hit.getBlockPos(), color);
		}
	}

	public void addEntity(Entity entity, KubeColor color) {
		if (entity != null) {
			int col = colorToInt(color);
			renderer.highlightedEntities.put(entity, col);
			renderer.uniqueColors.add(col);
		}
	}

	public void addEntities(EntitySelector selector, KubeColor color) {
		int col = colorToInt(color);
		boolean added = false;

		for (var entity : getPlayer().level().kjs$getEntities().filterSelector(selector)) {
			renderer.highlightedEntities.put(entity, col);
			added = true;
		}

		if (added) {
			renderer.uniqueColors.add(col);
		}
	}

	public void addEntitiesByType(EntityType<?> type, KubeColor color) {
		int col = colorToInt(color);
		boolean added = false;

		for (var entity : getPlayer().level().kjs$getEntities().filterType(type)) {
			renderer.highlightedEntities.put(entity, col);
			added = true;
		}

		if (added) {
			renderer.uniqueColors.add(col);
		}
	}

	public void addTargetEntity(KubeColor color) {
		if (mc.hitResult instanceof EntityHitResult hit) {
			addEntity(hit.getEntity(), color);
		}
	}

	public void addTarget(KubeColor color) {
		if (mc.hitResult instanceof EntityHitResult hit) {
			addEntity(hit.getEntity(), color);
		} else if (mc.hitResult instanceof BlockHitResult hit && mc.hitResult.getType() == HitResult.Type.BLOCK) {
			addBlock(hit.getBlockPos(), color);
		}
	}
}