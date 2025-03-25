package dev.latvian.mods.kubejs.client.icon;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.world.item.ItemStack;

public record ItemKubeIcon(ItemStack item) implements KubeIcon {
	public static final KubeIconType<ItemKubeIcon> TYPE = new KubeIconType<>(KubeJS.id("item"), RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemStack.CODEC.fieldOf("item").forGetter(ItemKubeIcon::item)
	).apply(instance, ItemKubeIcon::new)), ItemStack.STREAM_CODEC.map(ItemKubeIcon::new, ItemKubeIcon::item));

	@Override
	public KubeIconType<?> getType() {
		return TYPE;
	}
}
