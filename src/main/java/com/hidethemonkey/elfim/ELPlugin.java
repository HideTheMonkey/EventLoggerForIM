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

import com.hidethemonkey.elfim.commands.CommandELFS;
import com.hidethemonkey.elfim.listeners.PlayerEventListeners;
import com.hidethemonkey.elfim.listeners.ServerEventListeners;
import org.bukkit.plugin.java.JavaPlugin;

public class ELPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    saveDefaultConfig();
    ELConfig elConfig = new ELConfig(this.getConfig(), getLogger());
    // Save the plugin version to use in MessageHandler
    elConfig.setVersion(this.getDescription().getVersion());
    saveConfig();
    // Store name on config for easy access later (not saved to file)
    elConfig.setPluginName(this.getName());

    saveResource("advancements.yml", true);
    AdvancementConfig advConfig = new AdvancementConfig(getDataFolder());

    if (checkToken(elConfig.getToken()) && checkChannel(elConfig.getChannelId())) {
      // Register Server Events
      getServer()
          .getPluginManager()
          .registerEvents(new ServerEventListeners(elConfig), this);

      // Register Player Events
      getServer()
          .getPluginManager()
          .registerEvents(new PlayerEventListeners(elConfig, advConfig), this);
    } else {
      getLogger().warning("The Slack API token or channel is not configured!");
    }
    // Register configuration commands
    this.getCommand("elfs").setExecutor(new CommandELFS(elConfig));
  }

  @Override
  public void onDisable() {
    getServer().getScheduler().cancelTasks(this);
  }

  private boolean checkChannel(String channelId) {
    if (channelId.trim().isBlank() || channelId.trim().equals(ELConfig.defaultSlackChannelId)) {
      getLogger().severe("The Slack channel must be set in the plugin config.yml file!");
      return false;
    }
    return true;
  }

  private boolean checkToken(String token) {
    if (token.trim().isBlank() || token.trim().equals(ELConfig.defaultSlackToken)) {
      getLogger().severe("The Slack API token is not configured, please update it in the plugin config.yml file!");
      return false;
    }
    return true;
  }
}
