package com.github.lunatrius.profiles;

import com.github.lunatrius.profiles.lib.Reference;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumDifficulty;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile {
	private boolean invertMouse;
	private float mouseSensitivity;
	private float fov;
	private float gamma;
	private float saturation;
	private int renderDistance;
	private int guiScale;
	private int particles;
	private boolean viewBobbing;
	private boolean anaglyph;
	private boolean advancedOpengl;
	private int maxFps;
	private boolean fbo;
	private int difficulty;
	private boolean fancyGraphics;
	private int ambientOcclusion;
	private boolean clouds;
	private List<String> resourcePacks;
	private String language;
	private int chatVisibility;
	private boolean chatColors;
	private boolean chatLinks;
	private boolean chatLinksPrompt;
	private float chatOpacity;
	private boolean serverTextures;
	private boolean snooper;
	private boolean fullscreen;
	private boolean vsync;
	private boolean advancedItemTooltips;
	private boolean pauseOnLostFocus;
	private boolean showCape;
	private boolean touchscreen;
	private int overrideWidth;
	private int overrideHeight;
	private boolean heldItemTooltips;
	private float chatHeightFocused;
	private float chatHeightUnfocused;
	private float chatScale;
	private float chatWidth;
	private boolean showInventoryAchievementHint;
	private int mipmapLevels;
	private int anisotropicFiltering;
	private boolean forceUnicodeFont;
	private Map<SoundCategory, Float> volume;
	private Map<String, Object> optifine = new HashMap<String, Object>();

	private void fromGameSettingsOptifine(GameSettings gameSettings) {
		for (Field field : gameSettings.getClass().getFields()) {
			String name = field.getName();
			if (name.startsWith("of") && !name.equals("ofKeyBindZoom")) {
				try {
					this.optifine.put(name, field.get(gameSettings));
				} catch (Exception e) {
					Reference.logger.error(String.format("Can not get property '%s'!", name), e);
				}
			}
		}
	}

	private void toGameSettingsOptifine(GameSettings gameSettings) {
		for (Field field : gameSettings.getClass().getFields()) {
			String name = field.getName();
			if (name.startsWith("of") && !name.equals("ofKeyBindZoom")) {
				try {
					Object value = this.optifine.get(name);

					// JSON loads numbers as Double, have to correct that
					if (value instanceof Number) {
						if (field.getType() == int.class) {
							value = ((Number) value).intValue();
						} else if (field.getType() == float.class) {
							value = ((Number) value).floatValue();
						}
					}

					if (value != null) {
						field.set(gameSettings, value);
					}
				} catch (Exception e) {
					Reference.logger.error(String.format("Can not set property '%s'!", name), e);
				}
			}
		}
	}

	public static Profile fromGameSettings(GameSettings gameSettings) {
		Profile profile = new Profile();
		profile.invertMouse = gameSettings.invertMouse;
		profile.invertMouse = gameSettings.invertMouse;
		profile.mouseSensitivity = gameSettings.mouseSensitivity;
		profile.fov = gameSettings.fovSetting;
		profile.gamma = gameSettings.gammaSetting;
		profile.saturation = gameSettings.saturation;
		profile.renderDistance = gameSettings.renderDistanceChunks;
		profile.guiScale = gameSettings.guiScale;
		profile.particles = gameSettings.particleSetting;
		profile.viewBobbing = gameSettings.viewBobbing;
		profile.anaglyph = gameSettings.anaglyph;
		profile.advancedOpengl = gameSettings.advancedOpengl;
		profile.maxFps = gameSettings.limitFramerate;
		profile.fbo = gameSettings.fboEnable;
		profile.difficulty = gameSettings.difficulty.getDifficultyId();
		profile.fancyGraphics = gameSettings.fancyGraphics;
		profile.ambientOcclusion = gameSettings.ambientOcclusion;
		profile.clouds = gameSettings.clouds;
		profile.resourcePacks = gameSettings.resourcePacks;
		profile.language = gameSettings.language;
		profile.chatVisibility = gameSettings.chatVisibility.getChatVisibility();
		profile.chatColors = gameSettings.chatColours;
		profile.chatLinks = gameSettings.chatLinks;
		profile.chatLinksPrompt = gameSettings.chatLinksPrompt;
		profile.chatOpacity = gameSettings.chatOpacity;
		profile.serverTextures = gameSettings.serverTextures;
		profile.snooper = gameSettings.snooperEnabled;
		profile.fullscreen = gameSettings.fullScreen;
		profile.vsync = gameSettings.enableVsync;
		profile.advancedItemTooltips = gameSettings.advancedItemTooltips;
		profile.pauseOnLostFocus = gameSettings.pauseOnLostFocus;
		profile.showCape = gameSettings.showCape;
		profile.touchscreen = gameSettings.touchscreen;
		profile.overrideWidth = gameSettings.overrideWidth;
		profile.overrideHeight = gameSettings.overrideHeight;
		profile.heldItemTooltips = gameSettings.heldItemTooltips;
		profile.chatHeightFocused = gameSettings.chatHeightFocused;
		profile.chatHeightUnfocused = gameSettings.chatHeightUnfocused;
		profile.chatScale = gameSettings.chatScale;
		profile.chatWidth = gameSettings.chatWidth;
		profile.showInventoryAchievementHint = gameSettings.showInventoryAchievementHint;
		profile.mipmapLevels = gameSettings.mipmapLevels;
		profile.anisotropicFiltering = gameSettings.anisotropicFiltering;
		profile.forceUnicodeFont = gameSettings.forceUnicodeFont;
		profile.volume = new HashMap<SoundCategory, Float>();
		for (SoundCategory soundCategory : SoundCategory.values()) {
			profile.volume.put(soundCategory, gameSettings.getSoundLevel(soundCategory));
		}
		if (FMLClientHandler.instance().hasOptifine()) {
			profile.fromGameSettingsOptifine(gameSettings);
		}
		return profile;
	}

	public static GameSettings toGameSettings(Profile profile) {
		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
		gameSettings.invertMouse = profile.invertMouse;
		gameSettings.invertMouse = profile.invertMouse;
		gameSettings.mouseSensitivity = profile.mouseSensitivity;
		gameSettings.fovSetting = profile.fov;
		gameSettings.gammaSetting = profile.gamma;
		gameSettings.saturation = profile.saturation;
		gameSettings.renderDistanceChunks = profile.renderDistance;
		gameSettings.guiScale = profile.guiScale;
		gameSettings.particleSetting = profile.particles;
		gameSettings.viewBobbing = profile.viewBobbing;
		gameSettings.anaglyph = profile.anaglyph;
		gameSettings.advancedOpengl = profile.advancedOpengl;
		gameSettings.limitFramerate = profile.maxFps;
		gameSettings.fboEnable = profile.fbo;
		gameSettings.difficulty = EnumDifficulty.getDifficultyEnum(profile.difficulty);
		gameSettings.fancyGraphics = profile.fancyGraphics;
		gameSettings.ambientOcclusion = profile.ambientOcclusion;
		gameSettings.clouds = profile.clouds;
		if (profile.resourcePacks != null) {
			gameSettings.resourcePacks = profile.resourcePacks;
		}
		gameSettings.language = profile.language;
		gameSettings.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(profile.chatVisibility);
		gameSettings.chatColours = profile.chatColors;
		gameSettings.chatLinks = profile.chatLinks;
		gameSettings.chatLinksPrompt = profile.chatLinksPrompt;
		gameSettings.chatOpacity = profile.chatOpacity;
		gameSettings.serverTextures = profile.serverTextures;
		gameSettings.snooperEnabled = profile.snooper;
		gameSettings.fullScreen = profile.fullscreen;
		gameSettings.enableVsync = profile.vsync;
		gameSettings.advancedItemTooltips = profile.advancedItemTooltips;
		gameSettings.pauseOnLostFocus = profile.pauseOnLostFocus;
		gameSettings.showCape = profile.showCape;
		gameSettings.touchscreen = profile.touchscreen;
		gameSettings.overrideWidth = profile.overrideWidth;
		gameSettings.overrideHeight = profile.overrideHeight;
		gameSettings.heldItemTooltips = profile.heldItemTooltips;
		gameSettings.chatHeightFocused = profile.chatHeightFocused;
		gameSettings.chatHeightUnfocused = profile.chatHeightUnfocused;
		gameSettings.chatScale = profile.chatScale;
		gameSettings.chatWidth = profile.chatWidth;
		gameSettings.showInventoryAchievementHint = profile.showInventoryAchievementHint;
		gameSettings.mipmapLevels = profile.mipmapLevels;
		gameSettings.anisotropicFiltering = profile.anisotropicFiltering;
		gameSettings.forceUnicodeFont = profile.forceUnicodeFont;
		if (profile.volume != null) {
			for (Map.Entry<SoundCategory, Float> entry : profile.volume.entrySet()) {
				gameSettings.setSoundLevel(entry.getKey(), entry.getValue());
			}
		}
		if (FMLClientHandler.instance().hasOptifine()) {
			profile.toGameSettingsOptifine(gameSettings);
		}
		return gameSettings;
	}
}
