package com.github.lunatrius.profiles.reference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class Reference {
	public static final String MODID = "Profiles";
	public static final String NAME = "Profiles";
	public static final String VERSION = "${version}";
	public static final String FORGE = "${forgeversion}";
	public static final String MINECRAFT = "${mcversion}";

	public static Logger logger = LogManager.getLogger(Reference.MODID);
	public static File config = null;
}
