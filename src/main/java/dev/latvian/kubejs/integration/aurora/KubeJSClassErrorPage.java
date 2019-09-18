package dev.latvian.kubejs.integration.aurora;

import dev.latvian.kubejs.documentation.Documentation;
import dev.latvian.kubejs.script.ScriptModData;
import dev.latvian.mods.aurora.page.HTTPWebPage;
import dev.latvian.mods.aurora.tag.Tag;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * @author LatvianModder
 */
public class KubeJSClassErrorPage extends HTTPWebPage
{
	private final Documentation documentation;
	private final String className;

	public KubeJSClassErrorPage(Documentation d, String c)
	{
		documentation = d;
		className = c;
	}

	@Override
	public void head(Tag head)
	{
		head.paired("title", "KubeJS Documentation");
		head.unpaired("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "https://kubejs.latvian.dev/style.css");
		head.unpaired("link").attr("rel", "icon").attr("href", "https://kubejs.latvian.dev/logo_48.png");
	}

	@Override
	public void body(Tag body)
	{
		body.img("https://kubejs.latvian.dev/logo_title.png").style("height", "7em");
		body.br();
		body.h1("").a("KubeJS Documentation", "/");

		body.h1("Error!");
		Tag t = body.p();
		t.text("Class ");
		t.span(className, "type");
		t.text(" not found!");

		body.br();
		body.p().paired("i", "Hosted from '" + FMLCommonHandler.instance().getMinecraftServerInstance().getMOTD() + "'");
		body.p().paired("i", "Mod version: " + ScriptModData.getInstance().getModVersion());
		body.p().paired("i", "Mod loader: " + ScriptModData.getInstance().getType());
		body.p().paired("i", "Minecraft version: " + ScriptModData.getInstance().getMcVersion());
		body.p().paired("i").a("Visit kubejs.latvian.dev for more info about the mod", "https://kubejs.latvian.dev");
	}
}