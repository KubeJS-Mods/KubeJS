package dev.latvian.mods.kubejs.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import dev.latvian.mods.kubejs.client.NotificationToast;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.util.color.SimpleColor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public record NotificationToastData(
	Duration duration,
	Component text,
	IconKJS icon,
	int iconSize,
	Color outlineColor,
	Color borderColor,
	Color backgroundColor,
	boolean textShadow
) {
	public static final Duration DEFAULT_DURATION = Duration.ofSeconds(5L);
	public static final Color DEFAULT_BORDER_COLOR = new SimpleColor(0x472954);
	public static final Color DEFAULT_BACKGROUND_COLOR = new SimpleColor(0x241335);

	public static final MapCodec<NotificationToastData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		KubeJSCodecs.DURATION.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(NotificationToastData::duration),
		ComponentSerialization.CODEC.optionalFieldOf("text", Component.empty()).forGetter(NotificationToastData::text),
		IconKJS.CODEC.optionalFieldOf("icon", IconKJS.NONE).forGetter(NotificationToastData::icon),
		Codec.INT.optionalFieldOf("icon_size", 16).forGetter(NotificationToastData::iconSize),
		KubeJSCodecs.RHINO_COLOR.optionalFieldOf("outline_color", SimpleColor.BLACK).forGetter(NotificationToastData::outlineColor),
		KubeJSCodecs.RHINO_COLOR.optionalFieldOf("border_color", DEFAULT_BORDER_COLOR).forGetter(NotificationToastData::borderColor),
		KubeJSCodecs.RHINO_COLOR.optionalFieldOf("background_color", DEFAULT_BACKGROUND_COLOR).forGetter(NotificationToastData::backgroundColor),
		Codec.BOOL.optionalFieldOf("text_shadow", false).forGetter(NotificationToastData::textShadow)
	).apply(instance, NotificationToastData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, NotificationToastData> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public NotificationToastData decode(RegistryFriendlyByteBuf buf) {
			var duration = KubeJSCodecs.DURATION_STREAM.decode(buf);
			var text = ComponentSerialization.STREAM_CODEC.decode(buf);
			var icon = IconKJS.STREAM_CODEC.decode(buf);
			var iconSize = ByteBufCodecs.VAR_INT.decode(buf);
			var outlineColor = KubeJSCodecs.RHINO_COLOR_STREAM.decode(buf);
			var borderColor = KubeJSCodecs.RHINO_COLOR_STREAM.decode(buf);
			var backgroundColor = KubeJSCodecs.RHINO_COLOR_STREAM.decode(buf);
			var textShadow = ByteBufCodecs.BOOL.decode(buf);
			return new NotificationToastData(duration, text, icon, iconSize, outlineColor, borderColor, backgroundColor, textShadow);
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, NotificationToastData data) {
			KubeJSCodecs.DURATION_STREAM.encode(buf, data.duration());
			ComponentSerialization.STREAM_CODEC.encode(buf, data.text());
			IconKJS.STREAM_CODEC.encode(buf, data.icon());
			ByteBufCodecs.VAR_INT.encode(buf, data.iconSize());
			KubeJSCodecs.RHINO_COLOR_STREAM.encode(buf, data.outlineColor());
			KubeJSCodecs.RHINO_COLOR_STREAM.encode(buf, data.borderColor());
			KubeJSCodecs.RHINO_COLOR_STREAM.encode(buf, data.backgroundColor());
			ByteBufCodecs.BOOL.encode(buf, data.textShadow());
		}
	};

	public static NotificationToastData ofText(Component text) {
		return new NotificationToastData(DEFAULT_DURATION, text, IconKJS.NONE, 16, SimpleColor.BLACK, DEFAULT_BORDER_COLOR, DEFAULT_BACKGROUND_COLOR, false);
	}

	public static NotificationToastData ofTitle(Component title, @Nullable Component text) {
		return text == null ? ofText(title) : ofText(Component.empty().append(title).append("\n").append(text));
	}

	@OnlyIn(Dist.CLIENT)
	public void show() {
		var mc = Minecraft.getInstance();
		mc.getToasts().addToast(new NotificationToast(mc, this));
	}
}
