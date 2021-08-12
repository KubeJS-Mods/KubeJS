package dev.latvian.kubejs.client.painter.world;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.latvian.kubejs.client.painter.PaintEventJS;
import net.minecraft.client.Minecraft;

public class WorldPaintEventJS extends PaintEventJS {
	public WorldPaintEventJS(Minecraft m, PoseStack ps, float d) {
		super(m, ps, d, null);
	}

	public void scale(float scale) {
		scale(scale, scale, scale);
	}

	public void rotateDegX(float angle) {
		matrices.mulPose(Vector3f.XP.rotationDegrees(angle));
	}

	public void rotateRadX(float angle) {
		matrices.mulPose(Vector3f.XP.rotation(angle));
	}

	public void rotateDegY(float angle) {
		matrices.mulPose(Vector3f.YP.rotationDegrees(angle));
	}

	public void rotateRadY(float angle) {
		matrices.mulPose(Vector3f.YP.rotation(angle));
	}

	public void rotateDegZ(float angle) {
		matrices.mulPose(Vector3f.ZP.rotationDegrees(angle));
	}

	public void rotateRadZ(float angle) {
		matrices.mulPose(Vector3f.ZP.rotation(angle));
	}
}
