package dev.latvian.mods.kubejs.client.icon;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.data.AtlasIds;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public interface KubeIconRenderer {
	interface Registry {
		<T extends KubeIcon> void register(KubeIconType<T> type, Function<T, KubeIconRenderer> factory);
	}

	Lazy<Map<KubeIconType<?>, Function<?, KubeIconRenderer>>> RENDERERS = Lazy.map(map -> {
		Registry registry = map::put;
		registry.register(TextureKubeIcon.TYPE, FromTexture::new);
		registry.register(AtlasSpriteKubeIcon.TYPE, FromAtlasSprite::new);
		registry.register(ItemKubeIcon.TYPE, FromItem::new);
		// FIXME: Allow custom renderers
	});

	@Nullable
	static KubeIconRenderer from(KubeIcon icon) {
		var factory = RENDERERS.get().get(icon.getType());
		return factory != null ? factory.apply(Cast.to(icon)) : null;
	}

	void draw(Minecraft mc, GuiGraphicsExtractor graphics, int x, int y, int size);

	record FromTexture(TextureKubeIcon icon) implements KubeIconRenderer {
		@Override
		public void draw(Minecraft mc, GuiGraphicsExtractor graphics, int x, int y, int size) {
			int p0 = -size / 2;
			int p1 = p0 + size;

			graphics.blit(icon.texture(), x + p0, y + p0, x + p1, y + p1, 0F, 1F, 0F, 1F);
		}
	}

	record FromAtlasSprite(AtlasSpriteKubeIcon icon) implements KubeIconRenderer {
		@Override
		public void draw(Minecraft mc, GuiGraphicsExtractor graphics, int x, int y, int size) {
			TextureAtlas atlas = icon.atlas().isEmpty()
				? mc.getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS)
				: mc.getAtlasManager().getAtlasOrThrow(icon.atlas().get());

			var sprite = atlas.getSprite(icon.sprite());

			int p0 = -size / 2;
			int p1 = p0 + size;

			graphics.blit(sprite.atlasLocation(), x + p0, y + p0, x + p1, y + p1, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
		}
	}

	record FromItem(ItemKubeIcon icon) implements KubeIconRenderer {
		@Override
		public void draw(Minecraft mc, GuiGraphicsExtractor graphics, int x, int y, int size) {
			var m = RenderSystem.getModelViewStack();
			m.pushMatrix();
			m.translate(x - 2F, y + 2F, 0F);
			float s = size / 16F;
			m.scale(s, s, s);
			graphics.fakeItem(icon.item(), -8, -8);
			m.popMatrix();
		}
	}
}
