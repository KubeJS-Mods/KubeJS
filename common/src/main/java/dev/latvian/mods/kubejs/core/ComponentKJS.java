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
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * Extensions for components, will be injected into
 * {@link MutableComponent} at runtime.
 */
public interface ComponentKJS extends Component, Iterable<Component>, JsonSerializable, WrappedJS {

	@Override
	default Iterator<Component> iterator() {
		throw new NoMixinException();
	}

	default MutableComponent kjs$self() {
		return (MutableComponent) this;
	}

	@Override
	default JsonElement toJson() {
		return Component.Serializer.toJsonTree(kjs$self());
	}

	default boolean kjs$hasStyle() {
		return getStyle() != null && !getStyle().isEmpty();
	}

	default boolean kjs$hasSiblings() {
		return !getSiblings().isEmpty();
	}

	// region ChatFormatting extensions
	default MutableComponent black() {
		return kjs$self().withStyle(ChatFormatting.BLACK);
	}

	default MutableComponent darkBlue() {
		return kjs$self().withStyle(ChatFormatting.DARK_BLUE);
	}

	default MutableComponent darkGreen() {
		return kjs$self().withStyle(ChatFormatting.DARK_GREEN);
	}

	default MutableComponent darkAqua() {
		return kjs$self().withStyle(ChatFormatting.DARK_AQUA);
	}

	default MutableComponent darkRed() {
		return kjs$self().withStyle(ChatFormatting.DARK_RED);
	}

	default MutableComponent darkPurple() {
		return kjs$self().withStyle(ChatFormatting.DARK_PURPLE);
	}

	default MutableComponent gold() {
		return kjs$self().withStyle(ChatFormatting.GOLD);
	}

	default MutableComponent gray() {
		return kjs$self().withStyle(ChatFormatting.GRAY);
	}

	default MutableComponent darkGray() {
		return kjs$self().withStyle(ChatFormatting.DARK_GRAY);
	}

	default MutableComponent blue() {
		return kjs$self().withStyle(ChatFormatting.BLUE);
	}

	default MutableComponent green() {
		return kjs$self().withStyle(ChatFormatting.GREEN);
	}

	default MutableComponent aqua() {
		return kjs$self().withStyle(ChatFormatting.AQUA);
	}

	default MutableComponent red() {
		return kjs$self().withStyle(ChatFormatting.RED);
	}

	default MutableComponent lightPurple() {
		return kjs$self().withStyle(ChatFormatting.LIGHT_PURPLE);
	}

	default MutableComponent yellow() {
		return kjs$self().withStyle(ChatFormatting.YELLOW);
	}

	default MutableComponent white() {
		return kjs$self().withStyle(ChatFormatting.WHITE);
	}
	// endregion ChatFormatting extensions

	// region Style extensions
	default MutableComponent color(@Nullable Color c) {
		var col = c == null ? null : c.createTextColorJS();
		return kjs$self().setStyle(getStyle().withColor(col));
	}

	default MutableComponent noColor() {
		return color(null);
	}

	default MutableComponent bold(@Nullable Boolean value) {
		return kjs$self().setStyle(getStyle().withBold(value));
	}

	default MutableComponent bold() {
		return bold(true);
	}

	default MutableComponent italic(@Nullable Boolean value) {
		return kjs$self().setStyle(getStyle().withItalic(value));
	}

	default MutableComponent italic() {
		return italic(true);
	}

	default MutableComponent underlined(@Nullable Boolean value) {
		return kjs$self().setStyle(getStyle().withUnderlined(value));
	}

	default MutableComponent underlined() {
		return underlined(true);
	}

	default MutableComponent strikethrough(@Nullable Boolean value) {
		return kjs$self().setStyle(getStyle().withStrikethrough(value));
	}

	default MutableComponent strikethrough() {
		return strikethrough(true);
	}

	default MutableComponent obfuscated(@Nullable Boolean value) {
		return kjs$self().setStyle(getStyle().withObfuscated(value));
	}

	default MutableComponent obfuscated() {
		return obfuscated(true);
	}

	default MutableComponent insertion(@Nullable String s) {
		return kjs$self().setStyle(getStyle().withInsertion(s));
	}

	default MutableComponent font(@Nullable ResourceLocation s) {
		return kjs$self().setStyle(getStyle().withFont(s));
	}

	default MutableComponent click(@Nullable ClickEvent s) {
		return kjs$self().setStyle(getStyle().withClickEvent(s));
	}

	default MutableComponent hover(@Nullable Component s) {
		return kjs$self().setStyle(getStyle().withHoverEvent(s == null ? null : new HoverEvent(HoverEvent.Action.SHOW_TEXT, s)));
	}
	// endregion Style extensions
}
