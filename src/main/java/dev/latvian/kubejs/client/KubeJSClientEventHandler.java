package dev.latvian.kubejs.client;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.FieldJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ClientWorldJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class KubeJSClientEventHandler
{
	private static FieldJS<List<Widget>> buttons;

	public void init()
	{
		MinecraftForge.EVENT_BUS.addListener(this::bindings);
		MinecraftForge.EVENT_BUS.addListener(this::debugInfo);
		MinecraftForge.EVENT_BUS.addListener(this::itemTooltip);
		MinecraftForge.EVENT_BUS.addListener(this::clientTick);
		MinecraftForge.EVENT_BUS.addListener(this::loggedIn);
		MinecraftForge.EVENT_BUS.addListener(this::loggedOut);
		MinecraftForge.EVENT_BUS.addListener(this::respawn);
		MinecraftForge.EVENT_BUS.addListener(this::inGameScreenDraw);
		MinecraftForge.EVENT_BUS.addListener(this::guiScreenDraw);
	}

	private void bindings(BindingsEvent event)
	{
		event.add("client", new ClientWrapper());
	}

	private void debugInfo(RenderGameOverlayEvent.Text event)
	{
		if (Minecraft.getInstance().player != null)
		{
			new DebugInfoEventJS(event).post(ScriptType.CLIENT, KubeJSEvents.CLIENT_DEBUG_INFO);
		}
	}

	private void itemTooltip(ItemTooltipEvent event)
	{
		new ClientItemTooltipEventJS(event).post(ScriptType.CLIENT, KubeJSEvents.CLIENT_ITEM_TOOLTIP);
	}

	private void clientTick(TickEvent.ClientTickEvent event)
	{
		if (Minecraft.getInstance().player != null)
		{
			new ClientTickEventJS(ClientWorldJS.instance.clientPlayerData.getPlayer()).post(KubeJSEvents.CLIENT_TICK);
		}
	}

	private void loggedIn(ClientPlayerNetworkEvent.LoggedInEvent event)
	{
		ClientWorldJS.instance = new ClientWorldJS(Minecraft.getInstance(), event.getPlayer());
		MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(ClientWorldJS.instance));
		MinecraftForge.EVENT_BUS.post(new AttachPlayerDataEvent(ClientWorldJS.instance.clientPlayerData));
		new ClientLoggedInEventJS(ClientWorldJS.instance.clientPlayerData.getPlayer()).post(KubeJSEvents.CLIENT_LOGGED_IN);
	}

	private void loggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event)
	{
		ClientWorldJS.instance = null;
		KubeJSClient.activeOverlays.clear();
	}

	private void respawn(ClientPlayerNetworkEvent.RespawnEvent event)
	{
		ClientWorldJS.instance = new ClientWorldJS(Minecraft.getInstance(), event.getNewPlayer());
		MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(ClientWorldJS.instance));
		MinecraftForge.EVENT_BUS.post(new AttachPlayerDataEvent(ClientWorldJS.instance.clientPlayerData));
	}

	private int drawOverlay(Minecraft mc, int maxWidth, int x, int y, int p, Overlay o, boolean inv)
	{
		List<String> list = new ArrayList<>();
		int l = 10;

		for (Text t : o.text)
		{
			list.addAll(mc.fontRenderer.listFormattedStringToWidth(t.getFormattedString(), maxWidth));
		}

		int mw = 0;

		for (String s : list)
		{
			mw = Math.max(mw, mc.fontRenderer.getStringWidth(s));
		}

		if (mw == 0)
		{
			return 0;
		}

		int w = mw + p * 2;
		int h = list.size() * l + p * 2 - 2;
		int col = 0xFF000000 | o.color;
		int r = (col >> 16) & 0xFF;
		int g = (col >> 8) & 0xFF;
		int b = col & 0xFF;

		GlStateManager.disableTexture();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

		//o.color.withAlpha(200).draw(spx, spy, mw + p * 2, list.size() * l + p * 2 - 2);

		if (inv)
		{
			addRectToBuffer(buffer, x, y, w, h, r, g, b, 255);
			addRectToBuffer(buffer, x, y + 1, 1, h - 2, 0, 0, 0, 80);
			addRectToBuffer(buffer, x + w - 1, y + 1, 1, h - 2, 0, 0, 0, 80);
			addRectToBuffer(buffer, x, y, w, 1, 0, 0, 0, 80);
			addRectToBuffer(buffer, x, y + h - 1, w, 1, 0, 0, 0, 80);
		}
		else
		{
			addRectToBuffer(buffer, x, y, w, h, r, g, b, 200);
			addRectToBuffer(buffer, x, y + 1, 1, h - 2, r, g, b, 255);
			addRectToBuffer(buffer, x + w - 1, y + 1, 1, h - 2, r, g, b, 255);
			addRectToBuffer(buffer, x, y, w, 1, r, g, b, 255);
			addRectToBuffer(buffer, x, y + h - 1, w, 1, r, g, b, 255);
		}

		tessellator.draw();
		GlStateManager.enableTexture();

		for (int i = 0; i < list.size(); i++)
		{
			mc.fontRenderer.drawStringWithShadow(list.get(i), x + p, y + i * l + p, 0xFFFFFFFF);
		}

		return list.size() * l + p * 2 + (p - 2);
	}

	private void addRectToBuffer(BufferBuilder buffer, int x, int y, int w, int h, int r, int g, int b, int a)
	{
		buffer.pos(x, y + h, 0D).color(r, g, b, a).endVertex();
		buffer.pos(x + w, y + h, 0D).color(r, g, b, a).endVertex();
		buffer.pos(x + w, y, 0D).color(r, g, b, a).endVertex();
		buffer.pos(x, y, 0D).color(r, g, b, a).endVertex();
	}

	private void inGameScreenDraw(RenderGameOverlayEvent.Post event)
	{
		if (KubeJSClient.activeOverlays.isEmpty() || event.getType() != RenderGameOverlayEvent.ElementType.ALL)
		{
			return;
		}

		Minecraft mc = Minecraft.getInstance();

		if (mc.gameSettings.showDebugInfo || mc.currentScreen != null)
		{
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translatef(0, 0, 800);
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();

		int maxWidth = mc.mainWindow.getScaledWidth() / 4;
		int p = 4;
		int spx = p;
		int spy = p;

		for (Overlay o : KubeJSClient.activeOverlays.values())
		{
			spy += drawOverlay(mc, maxWidth, spx, spy, p, o, false);
		}

		GlStateManager.popMatrix();
	}

	private void guiScreenDraw(GuiScreenEvent.DrawScreenEvent.Post event)
	{
		if (KubeJSClient.activeOverlays.isEmpty())
		{
			return;
		}

		Minecraft mc = Minecraft.getInstance();

		GlStateManager.pushMatrix();
		GlStateManager.translatef(0, 0, 800);
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();

		int maxWidth = mc.mainWindow.getScaledWidth() / 4;
		int p = 4;
		int spx = p;
		int spy = p;

		if (buttons == null)
		{
			buttons = UtilsJS.getField(Screen.class, "buttons");
		}

		while (isOver(buttons.get(event.getGui()).orElse(Collections.emptyList()), spx, spy))
		{
			spy += 16;
		}

		for (Overlay o : KubeJSClient.activeOverlays.values())
		{
			if (o.alwaysOnTop)
			{
				spy += drawOverlay(mc, maxWidth, spx, spy, p, o, true);
			}
		}

		GlStateManager.popMatrix();
	}

	private boolean isOver(List<Widget> list, int x, int y)
	{
		for (Widget w : list)
		{
			if (w.visible && x >= w.x && y >= w.y && x < w.x + w.getWidth() && y < w.y + w.getHeight())
			{
				return true;
			}
		}

		return false;
	}
}