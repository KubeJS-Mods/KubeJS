package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.bindings.TextWrapper;
import dev.latvian.mods.kubejs.client.NotificationToast;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

public class NotificationBuilder {
	public static final Component[] NO_TEXT = new Component[0];
	public static final Duration DEFAULT_DURATION = Duration.ofSeconds(5L);
	public static final Color DEFAULT_BORDER_COLOR = new SimpleColor(0x472954);
	public static final Color DEFAULT_BACKGROUND_COLOR = new SimpleColor(0x241335);

	private static final int FLAG_ICON = 1;
	private static final int FLAG_TEXT_SHADOW = 2;
	private static final int FLAG_DURATION = 4;

	public static NotificationBuilder of(Context cx, Object object) {
		if (object instanceof NotificationBuilder b) {
			return b;
		} else if (object instanceof Map<?, ?> map) {
			return null; // FIXME
		} else if (object instanceof BaseFunction func) {
			Consumer consumer = (Consumer) NativeJavaObject.createInterfaceAdapter(cx, Consumer.class, func);
			var b = new NotificationBuilder();
			consumer.accept(b);
			return b;
		} else {
			var b = new NotificationBuilder();
			b.text = TextWrapper.of(object);
			return b;
		}
	}

	public static NotificationBuilder make(Consumer<NotificationBuilder> consumer) {
		var b = new NotificationBuilder();
		consumer.accept(b);
		return b;
	}

	public Duration duration;
	public Component text;
	public transient int iconType;
	public transient String icon;
	public int iconSize;
	public Color outlineColor;
	public Color borderColor;
	public Color backgroundColor;
	public boolean textShadow;

	public NotificationBuilder() {
		duration = DEFAULT_DURATION;
		text = Component.empty();
		iconType = 0;
		icon = "";
		iconSize = 16;
		outlineColor = SimpleColor.BLACK;
		borderColor = DEFAULT_BORDER_COLOR;
		backgroundColor = DEFAULT_BACKGROUND_COLOR;
		textShadow = true;
	}

	public NotificationBuilder(FriendlyByteBuf buf) {
		int flags = buf.readVarInt();
		text = buf.readComponent();

		duration = ((flags & FLAG_DURATION) != 0) ? Duration.ofMillis(buf.readVarLong()) : DEFAULT_DURATION;

		if ((flags & FLAG_ICON) != 0) {
			iconType = buf.readVarInt();
			icon = buf.readUtf();
			iconSize = buf.readByte();
		} else {
			iconType = 0;
			icon = "";
			iconSize = 16;
		}

		outlineColor = UtilsJS.readColor(buf);
		borderColor = UtilsJS.readColor(buf);
		backgroundColor = UtilsJS.readColor(buf);
		textShadow = (flags & FLAG_TEXT_SHADOW) != 0;
	}

	public void write(FriendlyByteBuf buf) {
		int flags = 0;

		if (iconType != 0) {
			flags |= FLAG_ICON;
		}

		if (textShadow) {
			flags |= FLAG_TEXT_SHADOW;
		}

		if (duration != DEFAULT_DURATION) {
			flags |= FLAG_DURATION;
		}

		buf.writeVarInt(flags);
		buf.writeComponent(text);

		if (duration != DEFAULT_DURATION) {
			buf.writeVarLong(duration.toMillis());
		}

		if (iconType != 0) {
			buf.writeVarInt(iconType);
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
