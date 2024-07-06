/*
 * MIT License
 *
 * Copyright (c) 2022 HideTheMonkey
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.hidethemonkey.elfim;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ELConfig {
  private final FileConfiguration config;
  private final Logger logger;
  private String pluginName = "";

  public static final String REPLACE_ME = "replace-me";
  public static final String ENABLE_STATS = "enableStats";
  public static final String MCUserAvatarUrlKey = "MCUserAvatarUrl";
  public static final String MCUserBustUrlKey = "MCUserBustUrl";
  public static final String SLACK = "slack";
  public static final String DISCORD = "discord";

  /**
   * @param fileConf
   * @param log
   */
  public ELConfig(FileConfiguration fileConf, Logger log) {
    this.config = fileConf;
    this.logger = log;
    config.addDefault(SLACK + ".apiToken", REPLACE_ME);
    config.addDefault(SLACK + ".channelId", REPLACE_ME);
    config.addDefault(ENABLE_STATS, true);
    config.options().copyDefaults(true);
  }

  /**
   *
   * @return
   */
  public String getGravatarEmail() {
    return config.getString("gravatarEmail");
  }

  /**
   *
   * @return
   */
  public String getGravatarUrl() {
    return config.getString("gravatarUrl");
  }

  /**
   * @return
   */
  public Set<String> getKeys() {
    return config.getKeys(true);
  }

  public List<String> getLogProperties() {
    return config.getStringList("logServerProperties");
  }

  /**
   *
   */
  private void save() {
    String confPath = config.getCurrentPath() + "plugins/" + getPluginName() + "/config.yml";
    try {
      config.save(confPath);
    } catch (IOException ioe) {
      logger.log(Level.SEVERE, "Could not save " + confPath, ioe);
    }
  }

  /**
   * @param key
   * @param value
   */
  public void setBoolean(String key, Boolean value) {
    config.set(key, value);
    save();
  }

  /**
   * @param key
   * @param value
   */
  public void setString(String key, String value) {
    config.set(key, value != null ? value.trim() : null);
    save();
  }

  /**
   * @return
   */
  public boolean getLogBroadcasts(String service) {
    return config.getBoolean(service + ".events.logBroadcasts");
  }

  /**
   * @return
   */
  public boolean getLogChat(String service) {
    return config.getBoolean(service + ".events.logChat");
  }

  /**
   * @return
   */
  public boolean getLogPlayerAdvancement(String service) {
    return config.getBoolean(service + ".events.logPlayerAdvancement");
  }

  /**
   * @return
   */
  public boolean getLogPlayerCommands(String service) {
    return config.getBoolean(service + ".events.logPlayerCommands");
  }

  /**
   * @return
   */
  public boolean getLogPlayerDeath(String service) {
    return config.getBoolean(service + ".events.logPlayerDeath");
  }

  /**
   * @return
   */
  public boolean getLogPlayerJoinLeave(String service) {
    return config.getBoolean(service + ".events.logPlayerJoinLeave");
  }

  /**
   * @return
   */
  public boolean getLogPlayerRespawn(String service) {
    return config.getBoolean(service + ".events.logPlayerRespawn");
  }

  /**
   * @return
   */
  public boolean getLogPlayerTeleport(String service) {
    return config.getBoolean(service + ".events.logPlayerTeleport");
  }

  /**
   * @return
   */
  public boolean getLogUnsuccessfulLogin(String service) {
    return config.getBoolean(service + ".events.logUnsuccessfulLogin");
  }

  /**
   * @return
   */
  public boolean getLogStartupPlugins(String service) {
    return config.getBoolean(service + ".events.logStartupPlugins");
  }

  /**
   * @return
   */
  public boolean getLogServerCommand(String service) {
    return config.getBoolean(service + ".events.logServerCommand");
  }

  /**
   * @return
   */
  public boolean getLogServerStartStop(String service) {
    return config.getBoolean(service + ".events.logServerStartStop");
  }

  /**
   *
   * @return
   */
  public String getPluginName() {
    return pluginName;
  }

  /**
   *
   * @param name
   */
  public void setPluginName(String name) {
    pluginName = name;
  }

  /**
   * @param uuid
   * @return
   */
  public String getMCUserAvatarUrl(String uuid) {
    if (uuid == null) {
      uuid = "";
    }
    return config.getString(ELConfig.MCUserAvatarUrlKey) + uuid;
  }

  /**
   * @param uuid
   * @return
   */
  public String getMCUserBustUrl(String uuid) {
    if (uuid == null) {
      uuid = "";
    }
    return config.getString(ELConfig.MCUserBustUrlKey) + uuid;
  }

  /**
   * @param msg
   */
  public void log(String msg) {
    logger.info(msg);
  }

  /**
   * 
   * @return
   */
  public boolean getEnableStats() {
    return config.getBoolean(ENABLE_STATS);
  }

  ////////////////////////////////////////////////
  // Slack
  ////////////////////////////////////////////////

  /**
   * @return
   */
  public boolean getSlackEnabled() {
    return config.getBoolean("enableSlack");
  }

  /**
   * @return
   */
  public String getSlackAPIToken() {
    return config.getString("slack.apiToken");
  }

  /**
   * @return
   */
  public String getSlackChannelId() {
    return config.getString("slack.channelId");
  }

  ////////////////////////////////////////////////
  // Discord
  ////////////////////////////////////////////////

  /**
   * @return
   */
  public boolean getDiscordEnabled() {
    return config.getBoolean("enableDiscord");
  }

  /**
   *
   * @return
   */
  public String getDiscordWebhookUrl() {
    return config.getString("discord.webhookUrl");
  }

  /**
   *
   * @return
   */
  public String getDiscordBotName() {
    return config.getString("discord.botUserName");
  }

  /**
   *
   * @return
   */
  public String getDiscordAvatarUrl() {
    return config.getString("discord.botAvatarUrl");
  }

  /**
   *
   * @param name
   * @return
   */
  public int getDiscordColor(String name) {
    return config.getInt("discord.colors." + name);
  }

  /**
   * @param plugin
   */
  public static void updateConfig(JavaPlugin plugin) {
    File file = new File(plugin.getDataFolder(), "config.yml");
    if (!file.exists()) {
      // Nothing to update...
      return;
    }
    YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(file);
    String configVersion = ymlConfig.getString("pluginVersion");
    if (configVersion == null || configVersion.isBlank()) {
      configVersion = "unknown";
    }
    String currentVersion = plugin.getDescription().getVersion();
    if (!currentVersion.equals(configVersion)) {
      // backup current config
      File sourceFile = new File(plugin.getDataFolder(), file.getName());
      File destFile = new File(plugin.getDataFolder() + "/config." + configVersion + ".yml");

      plugin.getLogger().info("Found mismatched plugin version, updating config...");
      plugin.getLogger().info("old: " + configVersion + ", new: " + currentVersion);
      plugin.getLogger().info("Backing up config.yml to " + destFile.getName());
      if (sourceFile.renameTo(destFile)) {
        plugin.saveResource(file.getName(), true);
        try {
          FileConfiguration fileConfig = plugin.getConfig();
          fileConfig.load(file);
          Set<String> keys = ymlConfig.getKeys(true);
          Object newValue;
          Object oldValue;
          plugin.getLogger().info("Restoring previous configuration settings...");
          for (String key : keys) {
            oldValue = fileConfig.get(key);
            newValue = ymlConfig.get(key);
            if (!key.equals("pluginVersion") &&
                !(newValue instanceof MemorySection) &&
                newValue != null &&
                !newValue.equals(oldValue) &&
                fileConfig.contains(key)) {
              plugin.getLogger().info("Updating " + key + " from " + oldValue + " to " + newValue);
              fileConfig.set(key, newValue);
            }
          }
          fileConfig.save(file);
          plugin.getLogger().info("Completed updating config.yml to latest version!");
        } catch (IOException | InvalidConfigurationException e) {
          plugin.getLogger().log(Level.SEVERE, "Error processing updated config.yml", e);
          // Something went wrong, so make sure the current config is saved. This could
          // overwrite
          // the current settings, but it's safer to have a current config.yml.
          plugin.saveResource(file.getName(), true);
        }
      } else {
        plugin.getLogger().warning("Failed to backup config.yml");
      }
    }
  }
}
