package dev.latvian.mods.kubejs.core;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.mod.util.color.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Extensions for components, will be injected into
 * {@link MutableComponent} at runtime.
 */
public interface ComponentKJS extends Iterable<Component>, JsonSerializable, WrappedJS {

	MutableComponent withStyle(ChatFormatting... args);

	Style getStyle();

	MutableComponent setStyle(Style arg);

	Component self();

	default JsonElement toJson() {
		return Component.Serializer.toJsonTree(self());
	}

	// region ChatFormatting extensions
	default MutableComponent black() {
		return withStyle(ChatFormatting.BLACK);
	}

	default MutableComponent darkBlue() {
		return withStyle(ChatFormatting.DARK_BLUE);
	}

	default MutableComponent darkGreen() {
		return withStyle(ChatFormatting.DARK_GREEN);
	}

	default MutableComponent darkAqua() {
		return withStyle(ChatFormatting.DARK_AQUA);
	}

	default MutableComponent darkRed() {
		return withStyle(ChatFormatting.DARK_RED);
	}

	default MutableComponent darkPurple() {
		return withStyle(ChatFormatting.DARK_PURPLE);
	}

	default MutableComponent gold() {
		return withStyle(ChatFormatting.GOLD);
	}

	default MutableComponent gray() {
		return withStyle(ChatFormatting.GRAY);
	}

	default MutableComponent darkGray() {
		return withStyle(ChatFormatting.DARK_GRAY);
	}

	default MutableComponent blue() {
		return withStyle(ChatFormatting.BLUE);
	}

	default MutableComponent green() {
		return withStyle(ChatFormatting.GREEN);
	}

	default MutableComponent aqua() {
		return withStyle(ChatFormatting.AQUA);
	}

	default MutableComponent red() {
		return withStyle(ChatFormatting.RED);
	}

	default MutableComponent lightPurple() {
		return withStyle(ChatFormatting.LIGHT_PURPLE);
	}

	default MutableComponent yellow() {
		return withStyle(ChatFormatting.YELLOW);
	}

	default MutableComponent white() {
		return withStyle(ChatFormatting.WHITE);
	}
	// endregion ChatFormatting extensions

	// region Style extensions
	default MutableComponent color(@Nullable Color c) {
		var col = c == null ? null : c.createTextColorJS();
		return setStyle(getStyle().withColor(col));
	}

	default MutableComponent noColor() {
		return color(null);
	}

	default MutableComponent bold(@Nullable Boolean value) {
		return setStyle(getStyle().withBold(value));
	}

	default MutableComponent bold() {
		return bold(true);
	}

	default MutableComponent italic(@Nullable Boolean value) {
		return setStyle(getStyle().withItalic(value));
	}

	default MutableComponent italic() {
		return italic(true);
	}

	default MutableComponent underlined(@Nullable Boolean value) {
		return setStyle(getStyle().withUnderlined(value));
	}

	default MutableComponent underlined() {
		return underlined(true);
	}

	default MutableComponent strikethrough(@Nullable Boolean value) {
		return setStyle(getStyle().withStrikethrough(value));
	}

	default MutableComponent strikethrough() {
		return strikethrough(true);
	}

	default MutableComponent obfuscated(@Nullable Boolean value) {
		return setStyle(getStyle().withObfuscated(value));
	}

	default MutableComponent obfuscated() {
		return obfuscated(true);
	}

	default MutableComponent insertion(@Nullable String s) {
		return setStyle(getStyle().withInsertion(s));
	}

	default MutableComponent font(@Nullable ResourceLocation s) {
		return setStyle(getStyle().withFont(s));
	}

	default MutableComponent click(@Nullable ClickEvent s) {
		return setStyle(getStyle().withClickEvent(s));
	}

	default MutableComponent hover(@Nullable Component s) {
		return setStyle(getStyle().withHoverEvent(s == null ? null : new HoverEvent(HoverEvent.Action.SHOW_TEXT, s)));
	}
	// endregion Style extensions
}
