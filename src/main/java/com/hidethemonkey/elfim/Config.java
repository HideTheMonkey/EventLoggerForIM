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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.hidethemonkey.elfim;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {
  private final FileConfiguration config;
  private final Logger logger;
  private String pluginName = "";

  public static final String defaultSlackToken = "xoxb-replace-me";
  public static final String defaultSlackChannelId = "change-me-to-a-channel-id";
  public static final String avatarUrlKey = "avatarUrl";
  public static final String bustUrlKey = "bustUrl";

  /**
   * @param fileConf
   * @param log
   */
  public Config(FileConfiguration fileConf, Logger log) {
    this.config = fileConf;
    this.logger = log;
    config.addDefault("slack.apiToken", defaultSlackToken);
    config.addDefault("slack.channelId", defaultSlackChannelId);
    config.options().copyDefaults(true);
  }

  /**
   * @return
   */
  public Set<String> getKeys() {
    return config.getKeys(true);
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
    config.set(key, value);
    save();
  }

  /**
   * @return
   */
  public boolean getLogBroadcasts() {
    return config.getBoolean("slack.events.logBroadcasts");
  }

  /**
   * @return
   */
  public boolean getLogChat() {
    return config.getBoolean("slack.events.logChat");
  }

  /**
   * @return
   */
  public boolean getLogPlayerAdvancement() {
    return config.getBoolean("slack.events.logPlayerAdvancement");
  }

  /**
   * @return
   */
  public boolean getLogPlayerCommands() {
    return config.getBoolean("slack.events.logPlayerCommands");
  }

  /**
   * @return
   */
  public boolean getLogPlayerDeath() {
    return config.getBoolean("slack.events.logPlayerDeath");
  }

  /**
   * @return
   */
  public boolean getLogPlayerJoinLeave() {
    return config.getBoolean("slack.events.logPlayerJoinLeave");
  }

  /**
   * @return
   */
  public boolean getLogUnsuccessfulLogin() {
    return config.getBoolean("slack.events.logUnsuccessfulLogin");
  }

  /**
   * @return
   */
  public boolean getLogServerCommand() {
    return config.getBoolean("slack.events.logServerCommand");
  }

  /**
   * @return
   */
  public boolean getLogServerStartStop() {
    return config.getBoolean("slack.events.logServerStartStop");
  }

  /**
   * @param version
   */
  public void setVersion(String version) {
    config.set("pluginVersion", version);
  }

  /**
   * @return
   */
  public String getVersion() {
    return config.getString("pluginVersion");
  }

  public String getPluginName() {
    return pluginName;
  }

  public void setPluginName(String name) {
    pluginName = name;
  }

  /**
   * @return
   */
  public String getToken() {
    return config.getString("slack.apiToken");
  }

  /**
   * @return
   */
  public String getChannelId() {
    return config.getString("slack.channelId");
  }

  /**
   * @param uuid
   * @return
   */
  public String getAvatarUrl(String uuid) {
    if (uuid == null) {
      uuid = "";
    }
    return config.getString(Config.avatarUrlKey) + uuid;
  }

  /**
   * @param uuid
   * @return
   */
  public String getBustUrl(String uuid) {
    if (uuid == null) {
      uuid = "";
    }
    return config.getString(Config.bustUrlKey) + uuid;
  }

  /**
   * @param msg
   */
  public void log(String msg) {
    logger.info(msg);
  }
}
