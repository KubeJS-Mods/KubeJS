package dev.latvian.mods.kubejs.client.icon;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record AtlasSpriteKubeIcon(Optional<ResourceLocation> atlas, ResourceLocation sprite) implements KubeIcon {
	public static final KubeIconType<AtlasSpriteKubeIcon> TYPE = new KubeIconType<>(KubeJS.id("atlas_sprite"), RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceLocation.CODEC.optionalFieldOf("atlas").forGetter(AtlasSpriteKubeIcon::atlas),
		ResourceLocation.CODEC.fieldOf("sprite").forGetter(AtlasSpriteKubeIcon::sprite)
	).apply(instance, AtlasSpriteKubeIcon::new)), StreamCodec.composite(
		ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC),
		AtlasSpriteKubeIcon::atlas,
		ResourceLocation.STREAM_CODEC,
		AtlasSpriteKubeIcon::sprite,
		AtlasSpriteKubeIcon::new
	));

	@Override
	public KubeIconType<?> getType() {
		return TYPE;
	}
}
