package dev.latvian.kubejs.documentation;

import net.minecraft.server.MinecraftServer;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author LatvianModder
 */
public class DocumentationServer
{
	public static final DocumentationServer INSTANCE = new DocumentationServer();

	private String ip = "";
	private int port = 48574;

	private NettyHttpServer server = null;

	public boolean isRunning()
	{
		return server != null;
	}

	public void stopServer()
	{
		if (server != null)
		{
			server.shutdown();
			server = null;
		}
	}

	public void startServer()
	{
		if (server == null)
		{
			server = new NettyHttpServer(port);
			server.start();
		}
	}

	public void setIp(String i)
	{
		ip = i;
	}

	public void setPort(int p)
	{
		port = p;
	}

	@Ignore
	public String getUrl(MinecraftServer server)
	{
		if (ip.isEmpty())
		{
			if (!server.isSinglePlayer())
			{
				try (Scanner scanner = new Scanner(new URL("https://api.ipify.org").openStream(), StandardCharsets.UTF_8.toString()))
				{
					scanner.useDelimiter("\\A");

					if (scanner.hasNext())
					{
						return "http://" + scanner.next() + ":" + port;
					}
				}
				catch (Exception ignored)
				{
				}
			}

			return "http://localhost:" + port;
		}

		return "http://" + ip + ":" + port;
	}
}