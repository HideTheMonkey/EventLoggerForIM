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

import com.hidethemonkey.elfim.command.CommandELFS;
import org.bukkit.plugin.java.JavaPlugin;

public class Webhook extends JavaPlugin {

  private String pluginVersion;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    Config config = new Config(this.getConfig(), getLogger());
    pluginVersion = this.getDescription().getVersion();
    // Save the plugin version to use in MessageHandler
    config.setVersion(pluginVersion);
    saveConfig();
    // Store name on config for easy access later (not saved to file)
    config.setPluginName(this.getName());

    saveResource("advancements.yml", true);
    AdvancementConfig advConfig = new AdvancementConfig(getDataFolder());

    if (checkToken(config.getToken()) && checkChannel(config.getChannelId())) {
      getServer()
          .getPluginManager()
          .registerEvents(new EventListener(config, advConfig, this.getName()), this);
      this.getCommand("elfs").setExecutor(new CommandELFS(config));
    } else {
      getLogger().warning("The Slack API token or channel is not configured!");
    }
  }

  @Override
  public void onDisable() {
    getServer().getScheduler().cancelTasks(this);
  }

  private boolean checkChannel(String channelId) {
    if (channelId.trim().isBlank() || channelId.trim().equals(Config.defaultSlackChannelId)) {
      getLogger().severe("The Slack channel must be set in the plugin config.yml file!");
      return false;
    }
    return true;
  }

  private boolean checkToken(String token) {
    if (token.trim().isBlank() || token.trim().equals(Config.defaultSlackToken)) {
      getLogger().severe("The Slack API token is not configured, please update it in the plugin config.yml file!");
      return false;
    }
    return true;
  }
}
