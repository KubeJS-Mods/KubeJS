package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ShapeOverrideCallbackJS {
	public BlockState block;
	public CollisionContext context;
	public List<AABB> shape;

	public ShapeOverrideCallbackJS(BlockState containerJS, CollisionContext context) {
		this.block = containerJS;
		this.context = context;
		this.shape = new ArrayList<>();
	}

	public CollisionContext getCollisionContext() {
		return this.context;
	}

	@Info("Set the shape of the block.")
	public void box(double x0, double y0, double z0, double x1, double y1, double z1, boolean scale16) {
		if (scale16) {
			shape.add(new AABB(x0 / 16D, y0 / 16D, z0 / 16D, x1 / 16D, y1 / 16D, z1 / 16D));
		} else {
			shape.add(new AABB(x0, y0, z0, x1, y1, z1));
		}
	}

	@Info("Set the shape of the block.")
	public void box(double x0, double y0, double z0, double x1, double y1, double z1) {
		box(x0, y0, z0, x1, y1, z1, true);
	}

	public Object getStateValue(String name) {
		AtomicReference<Object> returnValue = new AtomicReference<>();
		block.getProperties().forEach((v) -> {
			if(v.getName().matches(name)) returnValue.set(block.getValue(v));
		});
		return returnValue;
	}
}
