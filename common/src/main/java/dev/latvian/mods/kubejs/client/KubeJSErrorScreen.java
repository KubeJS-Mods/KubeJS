package dev.latvian.mods.kubejs.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Objects;

public class KubeJSErrorScreen extends Screen {
	public final ScriptType type;
	private MultiLineLabel multilineMessage;

	public KubeJSErrorScreen(ScriptType type) {
		super(Component.empty());
		this.type = type;
		this.multilineMessage = MultiLineLabel.EMPTY;
	}

	@Override
	public Component getNarrationMessage() {
		return Component.literal("There were KubeJS startup errors!");
	}

	@Override
	protected void init() {
		super.init();

		var list = new ArrayList<Component>();
		list.add(Component.literal("There were KubeJS startup errors ").append(Component.literal("[" + type.errors.size() + "]").withStyle(ChatFormatting.DARK_RED)).append("!"));

		var style = Style.EMPTY.withColor(0xD19893);
		var errors = new ArrayList<>(type.errors);

		for (int i = 0; i < errors.size(); i++) {
			list.add(Component.empty());
			list.add(Component.literal((i + 1) + ") ").withStyle(ChatFormatting.DARK_RED).append(Component.literal(errors.get(i)).withStyle(style)));
		}

		this.multilineMessage = MultiLineLabel.create(this.font, CommonComponents.joinLines(list), this.width - 12);
		int i = this.height - 26;

		if (CommonProperties.get().startupErrorReportUrl.isBlank()) {
			this.addRenderableWidget(new Button(this.width / 2 - 155, i, 150, 20, Component.literal("Open startup.log"), this::openLog));
			this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, i, 150, 20, Component.literal("Quit"), this::quit));
		} else {
			this.addRenderableWidget(new Button(this.width / 4 - 55, i, 100, 20, Component.literal("Open startup.log"), this::openLog));
			this.addRenderableWidget(new Button(this.width / 2 - 50, i, 100, 20, Component.literal("Report"), this::report));
			this.addRenderableWidget(new Button(this.width * 3 / 4 - 45, i, 100, 20, Component.literal("Quit"), this::quit));
		}
	}

	private void quit(Button button) {
		minecraft.stop();
	}

	private void report(Button button) {
		handleComponentClicked(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, CommonProperties.get().startupErrorReportUrl)));
	}

	private void openLog(Button button) {
		handleComponentClicked(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, type.getLogFile().toAbsolutePath().toString())));
	}

	@Override
	public void render(PoseStack stack, int i, int j, float f) {
		this.renderBackground(stack);
		this.multilineMessage.renderCentered(stack, this.width / 2, this.messageTop());
		super.render(stack, i, j, f);
	}

	private int titleTop() {
		int i = (this.height - this.messageHeight()) / 2;
		int var10000 = i - 20;
		Objects.requireNonNull(this.font);
		return Mth.clamp(var10000 - 9, 10, 80);
	}

	private int messageTop() {
		return this.titleTop() + 20;
	}

	private int messageHeight() {
		int var10000 = this.multilineMessage.getLineCount();
		Objects.requireNonNull(this.font);
		return var10000 * 9;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
