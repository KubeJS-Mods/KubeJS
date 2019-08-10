package com.latmod.mods.kubejs.util;

import javax.annotation.Nullable;
import javax.script.ScriptEngine;
import java.io.File;

public class ScriptFile
{
	private final String path;
	private final File file;
	private ScriptEngine script;

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

	public void setScript(@Nullable ScriptEngine engine)
	{
		script = engine;
	}

	@Nullable
	public ScriptEngine getScript()
	{
		return script;
	}
}