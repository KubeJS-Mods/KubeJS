package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.bindings.ComponentWrapper;
import dev.latvian.mods.kubejs.client.NotificationToast;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.time.Duration;
import java.util.function.Consumer;

public class NotificationBuilder {
	public static final Color DEFAULT_BORDER_COLOR = new SimpleColor(0x472954);
	public static final Color DEFAULT_BACKGROUND_COLOR = new SimpleColor(0x241335);

	public static NotificationBuilder of(Object object) {
		if (object instanceof NotificationBuilder b) {
			return b;
		} else {
			var b = new NotificationBuilder();
			b.title = ComponentWrapper.of(object);
			return b;
		}
	}

	public static NotificationBuilder make(Consumer<NotificationBuilder> consumer) {
		var b = new NotificationBuilder();
		consumer.accept(b);
		return b;
	}

	public Duration duration;
	public Component title;
	public Component subtitle;
	public transient int iconType;
	public transient String icon;
	public int iconSize;
	public Color outlineColor;
	public Color borderColor;
	public Color backgroundColor;

	public NotificationBuilder() {
		duration = Duration.ofSeconds(5L);
		title = Component.empty();
		subtitle = Component.empty();
		iconType = 0;
		icon = "";
		iconSize = 16;
		outlineColor = SimpleColor.BLACK;
		borderColor = DEFAULT_BORDER_COLOR;
		backgroundColor = DEFAULT_BACKGROUND_COLOR;
	}

	public NotificationBuilder(FriendlyByteBuf buf) {
		duration = Duration.ofMillis(buf.readVarLong());
		title = buf.readComponent();
		subtitle = buf.readComponent();
		iconType = buf.readVarInt();
		icon = iconType == 0 ? "" : buf.readUtf();
		iconSize = iconType == 0 ? 16 : buf.readByte();
		outlineColor = UtilsJS.readColor(buf);
		borderColor = UtilsJS.readColor(buf);
		backgroundColor = UtilsJS.readColor(buf);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeVarLong(duration.toMillis());
		buf.writeComponent(title);
		buf.writeComponent(subtitle);
		buf.writeVarInt(iconType);

		if (iconType != 0) {
			buf.writeUtf(icon);
			buf.writeByte(iconSize);
		}

		UtilsJS.writeColor(buf, outlineColor);
		UtilsJS.writeColor(buf, borderColor);
		UtilsJS.writeColor(buf, backgroundColor);
	}

	public void setIcon(String icon) {
		this.icon = icon;
		this.iconType = 1;
	}

	public void setItemIcon(ItemStack stack) {
		this.icon = stack.kjs$getId();

		if (stack.getCount() > 1) {
			this.icon = stack.getCount() + "x " + this.icon;
		}

		if (stack.getTag() != null) {
			this.icon = this.icon + " " + stack.getTag();
		}

		this.iconType = 2;
	}

	public void setAtlasIcon(String icon) {
		this.icon = icon;
		this.iconType = 3;
	}

	@Environment(EnvType.CLIENT)
	public void show() {
		var mc = Minecraft.getInstance();
		mc.getToasts().addToast(new NotificationToast(mc, this));
	}
}
