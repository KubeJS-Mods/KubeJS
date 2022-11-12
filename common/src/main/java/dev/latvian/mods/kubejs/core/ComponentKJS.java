package dev.latvian.mods.kubejs.core;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * Extensions for components, will be injected into
 * {@link MutableComponent} at runtime.
 */
@RemapPrefixForJS("kjs$")
public interface ComponentKJS extends Component, Iterable<Component>, JsonSerializable, WrappedJS {

	@Override
	default Iterator<Component> iterator() {
		throw new NoMixinException();
	}

	default MutableComponent kjs$self() {
		return (MutableComponent) this;
	}

	@Override
	@RemapForJS("toJson")
	default JsonElement toJsonJS() {
		return Component.Serializer.toJsonTree(kjs$self());
	}

	default boolean kjs$hasStyle() {
		return getStyle() != null && !getStyle().isEmpty();
	}

	default boolean kjs$hasSiblings() {
		return !getSiblings().isEmpty();
	}

	// region ChatFormatting extensions
	default MutableComponent kjs$black() {
		return kjs$self().withStyle(ChatFormatting.BLACK);
	}

	default MutableComponent kjs$darkBlue() {
		return kjs$self().withStyle(ChatFormatting.DARK_BLUE);
	}

	default MutableComponent kjs$darkGreen() {
		return kjs$self().withStyle(ChatFormatting.DARK_GREEN);
	}

	default MutableComponent kjs$darkAqua() {
		return kjs$self().withStyle(ChatFormatting.DARK_AQUA);
	}

	default MutableComponent kjs$darkRed() {
		return kjs$self().withStyle(ChatFormatting.DARK_RED);
	}

	default MutableComponent kjs$darkPurple() {
		return kjs$self().withStyle(ChatFormatting.DARK_PURPLE);
	}

	default MutableComponent kjs$gold() {
		return kjs$self().withStyle(ChatFormatting.GOLD);
	}

	default MutableComponent kjs$gray() {
		return kjs$self().withStyle(ChatFormatting.GRAY);
	}

	default MutableComponent kjs$darkGray() {
		return kjs$self().withStyle(ChatFormatting.DARK_GRAY);
	}

	default MutableComponent kjs$blue() {
		return kjs$self().withStyle(ChatFormatting.BLUE);
	}

	default MutableComponent kjs$green() {
		return kjs$self().withStyle(ChatFormatting.GREEN);
	}

	default MutableComponent kjs$aqua() {
		return kjs$self().withStyle(ChatFormatting.AQUA);
	}

	default MutableComponent kjs$red() {
		return kjs$self().withStyle(ChatFormatting.RED);
	}

	default MutableComponent kjs$lightPurple() {
		return kjs$self().withStyle(ChatFormatting.LIGHT_PURPLE);
	}

	default MutableComponent kjs$yellow() {
		return kjs$self().withStyle(ChatFormatting.YELLOW);
	}

	default MutableComponent kjs$white() {
		return kjs$self().withStyle(ChatFormatting.WHITE);
	}
	// endregion ChatFormatting extensions

	// region Style extensions
	default MutableComponent kjs$color(@Nullable Color c) {
		var col = c == null ? null : c.createTextColorJS();
		return kjs$self().setStyle(getStyle().withColor(col));
	}

	default MutableComponent kjs$noColor() {
		return kjs$color(null);
	}

	default MutableComponent kjs$bold(@Nullable Boolean value) {
		return kjs$self().setStyle(getStyle().withBold(value));
	}

	default MutableComponent kjs$bold() {
		return kjs$bold(true);
	}

	default MutableComponent kjs$italic(@Nullable Boolean value) {
		return kjs$self().setStyle(getStyle().withItalic(value));
	}

	default MutableComponent kjs$italic() {
		return kjs$italic(true);
	}

	default MutableComponent kjs$underlined(@Nullable Boolean value) {
		return kjs$self().setStyle(getStyle().withUnderlined(value));
	}

	default MutableComponent kjs$underlined() {
		return kjs$underlined(true);
	}

	default MutableComponent kjs$strikethrough(@Nullable Boolean value) {
		return kjs$self().setStyle(getStyle().withStrikethrough(value));
	}

	default MutableComponent kjs$strikethrough() {
		return kjs$strikethrough(true);
	}

	default MutableComponent kjs$obfuscated(@Nullable Boolean value) {
		return kjs$self().setStyle(getStyle().withObfuscated(value));
	}

	default MutableComponent kjs$obfuscated() {
		return kjs$obfuscated(true);
	}

	default MutableComponent kjs$insertion(@Nullable String s) {
		return kjs$self().setStyle(getStyle().withInsertion(s));
	}

	default MutableComponent kjs$font(@Nullable ResourceLocation s) {
		return kjs$self().setStyle(getStyle().withFont(s));
	}

	default MutableComponent kjs$click(@Nullable ClickEvent s) {
		return kjs$self().setStyle(getStyle().withClickEvent(s));
	}

	default MutableComponent kjs$hover(@Nullable Component s) {
		return kjs$self().setStyle(getStyle().withHoverEvent(s == null ? null : new HoverEvent(HoverEvent.Action.SHOW_TEXT, s)));
	}
	// endregion Style extensions

	// These following methods only exist for interoperability with old scripts using the Text class
	// region Deprecated
	@Deprecated(forRemoval = true)
	default MutableComponent kjs$rawComponent() {
		KubeJS.LOGGER.warn("Using rawComponent() is deprecated, since components no longer need to be wrapped to Text! You can safely remove this method.");
		return kjs$self();
	}

	@Deprecated(forRemoval = true)
	default MutableComponent kjs$rawCopy() {
		KubeJS.LOGGER.warn("Using rawCopy() is deprecated, since components no longer need to be wrapped to Text! Use copy() instead.");
		return copy();
	}

	@Deprecated(forRemoval = true)
	default Component kjs$component() {
		KubeJS.LOGGER.warn("Using component() is deprecated, since components no longer need to be wrapped to Text! You can safely remove this method.");
		return kjs$self();
	}
	// endregion Deprecated
}
