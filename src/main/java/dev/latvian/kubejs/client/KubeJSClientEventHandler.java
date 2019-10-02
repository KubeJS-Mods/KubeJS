package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.item.BlockItemJS;
import dev.latvian.kubejs.item.ItemJS;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.FieldJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.ClientWorldJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID, value = Side.CLIENT)
public class KubeJSClientEventHandler
{
	private static final FieldJS buttonList = UtilsJS.getField(GuiScreen.class, "buttonList", "field_146292_n");

	@SubscribeEvent
	public static void onBindings(BindingsEvent event)
	{
		event.add("client", new ClientWrapper());
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		for (Item item : Item.REGISTRY)
		{
			if (item instanceof ItemJS)
			{
				ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(((ItemJS) item).properties.model));
			}
			else if (item instanceof BlockItemJS)
			{
				ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(((BlockItemJS) item).properties.model));
			}
		}
	}

	@SubscribeEvent
	public static void debugInfo(RenderGameOverlayEvent.Text event)
	{
		if (Minecraft.getMinecraft().player != null)
		{
			ClientWorldJS.get();
			EventsJS.post(KubeJSEvents.CLIENT_DEBUG_INFO, new DebugInfoEventJS(event));
		}
	}

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event)
	{
		if (Minecraft.getMinecraft().player != null)
		{
			EventsJS.post(KubeJSEvents.CLIENT_TICK, new ClientTickEventJS(ClientWorldJS.get().clientPlayerData.getPlayer()));
		}
	}

	@SubscribeEvent
	public static void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
	{
		ClientWorldJS.invalidate();
		KubeJSClient.activeOverlays.clear();
	}

	private static int drawOverlay(Minecraft mc, int maxWidth, int x, int y, int p, Overlay o, boolean inv)
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

		GlStateManager.disableTexture2D();
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
		GlStateManager.enableTexture2D();

		for (int i = 0; i < list.size(); i++)
		{
			mc.fontRenderer.drawStringWithShadow(list.get(i), x + p, y + i * l + p, 0xFFFFFFFF);
		}

		return list.size() * l + p * 2 + (p - 2);
	}

	public static void addRectToBuffer(BufferBuilder buffer, int x, int y, int w, int h, int r, int g, int b, int a)
	{
		buffer.pos(x, y + h, 0D).color(r, g, b, a).endVertex();
		buffer.pos(x + w, y + h, 0D).color(r, g, b, a).endVertex();
		buffer.pos(x + w, y, 0D).color(r, g, b, a).endVertex();
		buffer.pos(x, y, 0D).color(r, g, b, a).endVertex();
	}

	@SubscribeEvent
	public static void onInGameScreenDraw(RenderGameOverlayEvent.Post event)
	{
		if (KubeJSClient.activeOverlays.isEmpty() || event.getType() != RenderGameOverlayEvent.ElementType.ALL)
		{
			return;
		}

		Minecraft mc = Minecraft.getMinecraft();

		if (mc.gameSettings.showDebugInfo || mc.currentScreen != null)
		{
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 800D);
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();

		int maxWidth = event.getResolution().getScaledWidth() / 4;
		int p = 4;
		int spx = p;
		int spy = p;

		for (Overlay o : KubeJSClient.activeOverlays.values())
		{
			spy += drawOverlay(mc, maxWidth, spx, spy, p, o, false);
		}

		GlStateManager.popMatrix();
	}

	@SubscribeEvent
	public static void onGuiScreenDraw(GuiScreenEvent.DrawScreenEvent.Post event)
	{
		if (KubeJSClient.activeOverlays.isEmpty())
		{
			return;
		}

		Minecraft mc = Minecraft.getMinecraft();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 800D);
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();

		int maxWidth = new ScaledResolution(mc).getScaledWidth() / 4;
		int p = 4;
		int spx = p;
		int spy = p;

		List<GuiButton> list = buttonList.get(event.getGui());

		while (isOver(list, spx, spy))
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

	private static boolean isOver(List<GuiButton> list, int x, int y)
	{
		for (GuiButton button : list)
		{
			if (button.enabled && button.visible && x >= button.x && y >= button.y && x < button.x + button.width && y < button.y + button.height)
			{
				return true;
			}
		}

		return false;
	}
}