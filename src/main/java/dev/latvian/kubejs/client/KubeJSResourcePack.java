package dev.latvian.kubejs.client;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.script.ScriptManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackFileNotFoundException;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class KubeJSResourcePack implements IResourcePack
{
	private final File folder;
	private final ScriptManager scriptManager;

	public KubeJSResourcePack(File f, ScriptManager s)
	{
		folder = f;
		scriptManager = s;
	}

	@Override
	public String getPackName()
	{
		return "KubeJSResourcePack";
	}

	@Nullable
	private File getFile(ResourceLocation id)
	{
		File f = id.getNamespace().equals("kubejs") ? new File(folder, id.getPath()) : null;

		if (f != null && f.exists() && f.isFile())
		{
			return f;
		}

		return null;
	}

	@Override
	public InputStream getInputStream(ResourceLocation id) throws IOException
	{
		File file = getFile(id);

		if (file != null)
		{
			return new BufferedInputStream(new FileInputStream(file));
		}

		throw new ResourcePackFileNotFoundException(folder, id.toString());
	}

	@Override
	public boolean resourceExists(ResourceLocation id)
	{
		return getFile(id) != null;
	}

	@Override
	public Set<String> getResourceDomains()
	{
		return Collections.singleton(KubeJS.MOD_ID);
	}

	@Nullable
	@Override
	public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer serializer, String section)
	{
		JsonObject json = new JsonObject();
		JsonObject pack = new JsonObject();
		pack.addProperty("description", "KubeJS Resources");
		pack.addProperty("pack_format", 4);
		json.add("pack", pack);
		return serializer.parseMetadataSection(section, json);
	}

	@Override
	public BufferedImage getPackImage() throws IOException
	{
		return ImageIO.read(KubeJS.class.getResourceAsStream("/assets/kubejs/textures/logo.png"));
	}
}
