package com.github.lunatrius.profiles.command;

import com.github.lunatrius.profiles.Profile;
import com.github.lunatrius.profiles.lib.Reference;
import com.github.lunatrius.profiles.lib.Strings;
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
	private final Gson gson;
	private Map<String, Profile> profiles;

	public ProfileCommand() {
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		this.profiles = readFile(Reference.config);
	}

	@Override
	public String getCommandName() {
		return Strings.COMMAND_PROFILE;
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return Strings.COMMAND_PROFILE_USAGE;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender commandSender, String[] args) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, Strings.COMMAND_PROFILE_LIST, Strings.COMMAND_PROFILE_LOAD, Strings.COMMAND_PROFILE_SAVE, Strings.COMMAND_PROFILE_DELETE);
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase(Strings.COMMAND_PROFILE_LOAD) || args[0].equalsIgnoreCase(Strings.COMMAND_PROFILE_SAVE) || args[0].equalsIgnoreCase(Strings.COMMAND_PROFILE_DELETE)) {
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
			if (args[0].equalsIgnoreCase(Strings.COMMAND_PROFILE_LIST)) {
				this.profiles = readFile(Reference.config);

				if (this.profiles.size() == 0) {
					commandSender.addChatMessage(new ChatComponentTranslation(Strings.COMMAND_PROFILE_LIST_EMPTY));
					return;
				}

				TreeSet<String> sortedProfiles = getSortedProfiles();
				commandSender.addChatMessage(new ChatComponentTranslation(Strings.COMMAND_PROFILE_LIST_PROFILES, sortedProfiles.size()));
				for (String name : sortedProfiles) {
					commandSender.addChatMessage(new ChatComponentTranslation(Strings.COMMAND_PROFILE_LIST_ENTRY, name));
				}

				return;
			} else if (args[0].equalsIgnoreCase(Strings.COMMAND_PROFILE_LOAD)) {
				this.profiles = readFile(Reference.config);

				if (args.length > 1) {
					if (!this.profiles.containsKey(args[1])) {
						commandSender.addChatMessage(new ChatComponentTranslation(Strings.COMMAND_PROFILE_LOAD_INVALID, args[1]));
						return;
					}

					Profile.toGameSettings(this.profiles.get(args[1])).saveOptions();

					commandSender.addChatMessage(new ChatComponentTranslation(Strings.COMMAND_PROFILE_LOAD_SUCCESS, args[1]));
					return;
				}

				throw new WrongUsageException(Strings.COMMAND_PROFILE_LOAD_USAGE);
			} else if (args[0].equalsIgnoreCase(Strings.COMMAND_PROFILE_SAVE)) {
				this.profiles = readFile(Reference.config);

				if (args.length > 1) {
					this.profiles.put(args[1], Profile.fromGameSettings(Minecraft.getMinecraft().gameSettings));

					saveFile(Reference.config);

					commandSender.addChatMessage(new ChatComponentTranslation(Strings.COMMAND_PROFILE_SAVE_SUCCESS, args[1]));
					return;
				}

				throw new WrongUsageException(Strings.COMMAND_PROFILE_SAVE_USAGE);
			} else if (args[0].equalsIgnoreCase(Strings.COMMAND_PROFILE_DELETE)) {
				this.profiles = readFile(Reference.config);

				if (args.length > 1) {
					if (this.profiles.remove(args[1]) == null) {
						commandSender.addChatMessage(new ChatComponentTranslation(Strings.COMMAND_PROFILE_DELETE_INVALID, args[1]));
						return;
					}

					saveFile(Reference.config);

					commandSender.addChatMessage(new ChatComponentTranslation(Strings.COMMAND_PROFILE_DELETE_SUCCESS, args[1]));
					return;
				}

				throw new WrongUsageException(Strings.COMMAND_PROFILE_DELETE_USAGE);
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

				return this.gson.fromJson(str, new TypeToken<HashMap<String, Profile>>() {}.getType());
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
					e.printStackTrace();
				}
			}
		}
	}
}
