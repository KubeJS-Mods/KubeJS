package com.latmod.mods.kubejs.util;

import jdk.nashorn.api.scripting.NashornScriptEngine;

import javax.annotation.Nullable;
import java.io.File;

public class ScriptFile
{
	private final String path;
	private final File file;
	private NashornScriptEngine script;

	public ScriptFile(String p, File f)
	{
		path = p;
		file = f;
	}

	public String getPath()
	{
		return path;
	}

	public File getFile()
	{
		return file;
	}

	public void setScript(@Nullable NashornScriptEngine engine)
	{
		script = engine;
	}

	@Nullable
	public NashornScriptEngine getScript()
	{
		return script;
	}
}