package dev.latvian.mods.kubejs.client.icon;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.resources.Identifier;

public record TextureKubeIcon(Identifier texture) implements KubeIcon {
	public static final KubeIconType<TextureKubeIcon> TYPE = new KubeIconType<>(KubeJS.id("texture"), RecordCodecBuilder.mapCodec(instance -> instance.group(
		Identifier.CODEC.fieldOf("texture").forGetter(TextureKubeIcon::texture)
	).apply(instance, TextureKubeIcon::new)), Identifier.STREAM_CODEC.map(TextureKubeIcon::new, TextureKubeIcon::texture));

	@Override
	public KubeIconType<?> getType() {
		return TYPE;
	}
}
