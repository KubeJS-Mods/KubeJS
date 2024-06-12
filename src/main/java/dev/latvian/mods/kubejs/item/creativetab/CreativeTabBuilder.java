package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

@ReturnsSelf
public class CreativeTabBuilder extends BuilderBase<CreativeModeTab> {
	public transient CreativeTabIconSupplier icon;
	public transient CreativeTabContentSupplier content;

	public CreativeTabBuilder(ResourceLocation i) {
		super(i);
		this.icon = CreativeTabIconSupplier.DEFAULT;
		this.content = CreativeTabContentSupplier.DEFAULT;
	}

	@Override
	public final RegistryInfo getRegistryType() {
		return RegistryInfo.CREATIVE_MODE_TAB;
	}

	@Override
	public CreativeModeTab createObject() {
		return CreativeModeTab.builder()
			.title(displayName == null ? Component.translatable(getBuilderTranslationKey()) : displayName)
			.icon(new CreativeTabIconSupplier.Wrapper(icon))
			.displayItems(new CreativeTabContentSupplier.Wrapper(content))
			.build();
	}

	public CreativeTabBuilder icon(CreativeTabIconSupplier icon) {
		this.icon = icon;
		return this;
	}

	public CreativeTabBuilder content(CreativeTabContentSupplier content) {
		this.content = content;
		return this;
	}
}
