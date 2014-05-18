package com.github.lunatrius.profiles.lib;

import com.google.common.base.Throwables;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

public class Reference {
	static {
		Properties prop = new Properties();

		try {
			InputStream stream = Reference.class.getClassLoader().getResourceAsStream("version.properties");
			prop.load(stream);
			stream.close();
		} catch (Exception e) {
			Throwables.propagate(e);
		}

		VERSION = prop.getProperty("version.mod");
		FORGE = prop.getProperty("version.forge");
		MINECRAFT = prop.getProperty("version.minecraft");
	}

	public static final String MODID = "Profiles";
	public static final String NAME = "Profiles";
	public static final String VERSION;
	public static final String FORGE;
	public static final String MINECRAFT;

	public static Logger logger = null;
	public static File config = null;
}
