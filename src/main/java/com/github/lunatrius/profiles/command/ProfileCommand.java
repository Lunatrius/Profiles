package com.github.lunatrius.profiles.command;

import com.github.lunatrius.profiles.Profile;
import com.github.lunatrius.profiles.reference.Reference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentTranslation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class ProfileCommand extends CommandBase {
	public static final String NAME = "profile";

	public static final String LIST = "list";
	public static final String LOAD = "load";
	public static final String SAVE = "save";
	public static final String DELETE = "delete";

	public static final String USAGE = "commands.profiles.usage";

	public static final String LIST_EMPTY = "commands.profiles.list.empty";
	public static final String LIST_PROFILES = "commands.profiles.list.profiles";
	public static final String LIST_ENTRY = "commands.profiles.list.entry";

	public static final String LOAD_USAGE = "commands.profiles.load.usage";
	public static final String LOAD_INVALID = "commands.profiles.load.invalid";
	public static final String LOAD_SUCCESS = "commands.profiles.load.success";

	public static final String SAVE_USAGE = "commands.profiles.save.usage";
	public static final String SAVE_SUCCESS = "commands.profiles.save.success";

	public static final String DELETE_USAGE = "commands.profiles.delete.usage";
	public static final String DELETE_INVALID = "commands.profiles.delete.invalid";
	public static final String DELETE_SUCCESS = "commands.profiles.delete.success";

	private final Gson gson;
	private Map<String, Profile> profiles;

	public ProfileCommand() {
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		this.profiles = readFile(Reference.config);
	}

	@Override
	public String getCommandName() {
		return NAME;
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return USAGE;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender commandSender, String[] args) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, LIST, LOAD, SAVE, DELETE);
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase(LOAD) || args[0].equalsIgnoreCase(SAVE) || args[0].equalsIgnoreCase(DELETE)) {
				TreeSet<String> sortedProfiles = getSortedProfiles();
				String[] profileNames = new String[sortedProfiles.size()];
				return getListOfStringsMatchingLastWord(args, sortedProfiles.toArray(profileNames));
			}
		}

		return null;
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase(LIST)) {
				this.profiles = readFile(Reference.config);

				if (this.profiles.size() == 0) {
					commandSender.addChatMessage(new ChatComponentTranslation(LIST_EMPTY));
					return;
				}

				TreeSet<String> sortedProfiles = getSortedProfiles();
				commandSender.addChatMessage(new ChatComponentTranslation(LIST_PROFILES, sortedProfiles.size()));
				for (String name : sortedProfiles) {
					commandSender.addChatMessage(new ChatComponentTranslation(LIST_ENTRY, name));
				}

				return;
			} else if (args[0].equalsIgnoreCase(LOAD)) {
				this.profiles = readFile(Reference.config);

				if (args.length > 1) {
					Profile profile = this.profiles.get(args[1]);
					if (profile == null) {
						commandSender.addChatMessage(new ChatComponentTranslation(LOAD_INVALID, args[1]));
						return;
					}

					Profile.toGameSettings(profile).saveOptions();

					commandSender.addChatMessage(new ChatComponentTranslation(LOAD_SUCCESS, args[1]));
					return;
				}

				throw new WrongUsageException(LOAD_USAGE);
			} else if (args[0].equalsIgnoreCase(SAVE)) {
				this.profiles = readFile(Reference.config);

				if (args.length > 1) {
					this.profiles.put(args[1], Profile.fromGameSettings(Minecraft.getMinecraft().gameSettings));

					saveFile(Reference.config);

					commandSender.addChatMessage(new ChatComponentTranslation(SAVE_SUCCESS, args[1]));
					return;
				}

				throw new WrongUsageException(SAVE_USAGE);
			} else if (args[0].equalsIgnoreCase(DELETE)) {
				this.profiles = readFile(Reference.config);

				if (args.length > 1) {
					if (this.profiles.remove(args[1]) == null) {
						commandSender.addChatMessage(new ChatComponentTranslation(DELETE_INVALID, args[1]));
						return;
					}

					saveFile(Reference.config);

					commandSender.addChatMessage(new ChatComponentTranslation(DELETE_SUCCESS, args[1]));
					return;
				}

				throw new WrongUsageException(DELETE_USAGE);
			}
		}

		throw new WrongUsageException(getCommandUsage(commandSender));
	}

	private TreeSet<String> getSortedProfiles() {
		return new TreeSet<String>(this.profiles.keySet());
	}

	private Map<String, Profile> readFile(File file) {
		BufferedReader buffer = null;
		try {
			if (file.getParentFile() != null) {
				if (!file.getParentFile().mkdirs()) {
					Reference.logger.debug("Could not create directory!");
				}
			}

			if (!file.exists() && !file.createNewFile()) {
				return new HashMap<String, Profile>();
			}

			if (file.canRead()) {
				FileReader fileReader = new FileReader(file);
				buffer = new BufferedReader(fileReader);

				String str = "";

				String line;
				while ((line = buffer.readLine()) != null) {
					str += line + "\n";
				}

				Map<String, Profile> profiles = this.gson.fromJson(str, new TypeToken<HashMap<String, Profile>>() {}.getType());
				if (profiles != null) {
					return profiles;
				}

				return new HashMap<String, Profile>();
			}
		} catch (IOException e) {
			Reference.logger.error("IO failure!", e);
		} catch (JsonSyntaxException e) {
			Reference.logger.error(String.format("Malformed JSON in %s!", file.getName()), e);
		} finally {
			if (buffer != null) {
				try {
					buffer.close();
				} catch (IOException e) {
					Reference.logger.error("IO failure!", e);
				}
			}
		}

		return new HashMap<String, Profile>();
	}

	private void saveFile(File file) {
		String json = this.gson.toJson(this.profiles);
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(json);
		} catch (IOException e) {
			Reference.logger.error("", e);
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					Reference.logger.error("", e);
				}
			}
		}
	}
}
