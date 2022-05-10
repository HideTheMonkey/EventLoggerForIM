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
import com.hidethemonkey.elfim.listeners.SlackPlayerListeners;
import com.hidethemonkey.elfim.listeners.SlackServerListeners;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ELPlugin extends JavaPlugin {

  /**
   *
   */
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

    if (elConfig.getSlackEnabled()) {
      if (checkToken(elConfig.getToken()) && checkChannel(elConfig.getChannelId())) {
        // Register Slack Server Events
        registerSlackServerListeners(elConfig, getServer().getPluginManager());

        // Register Slack Player Events
        registerSlackPlayerListeners(elConfig, advConfig, getServer().getPluginManager());
      } else {
        getLogger().warning("The Slack API token or channel is not configured!");
      }
    }
    else {
      getLogger().info("Slack integration is not enabled.");
    }

    // Register configuration commands
    Objects.requireNonNull(this.getCommand("elfs")).setExecutor(new CommandELFS(elConfig));
  }

  /**
   *
   */
  @Override
  public void onDisable() {
    getServer().getScheduler().cancelTasks(this);
  }

  /**
   * @param config
   * @param manager
   */
  private void registerSlackServerListeners(ELConfig config, PluginManager manager) {
    SlackServerListeners serverListeners = new SlackServerListeners(config);
    if (config.getLogServerStartStop()) {
      manager.registerEvents(serverListeners.new PluginEnableDisableListener(), this);
    }

    if (config.getLogBroadcasts()) {
      manager.registerEvents(serverListeners.new BroadcastMessageListener(), this);
    }

    if (config.getLogServerCommand()) {
      manager.registerEvents(serverListeners.new ServerCommandListener(), this);
    }
  }

  /**
   *
   * @param config
   * @param advConfig
   * @param manager
   */
  private void registerSlackPlayerListeners(ELConfig config, AdvancementConfig advConfig, PluginManager manager) {
    SlackPlayerListeners playerListeners = new SlackPlayerListeners(config, advConfig);
    if (config.getLogPlayerJoinLeave()) {
      manager.registerEvents(playerListeners.new PlayerJoinListener(), this);
      manager.registerEvents(playerListeners.new PlayerQuitListener(), this);
    }

    if (config.getLogUnsuccessfulLogin()) {
      manager.registerEvents(playerListeners.new PlayerLoginListener(), this);
    }

    if (config.getLogChat()) {
      manager.registerEvents(playerListeners.new AsyncPlayerChatListener(), this);
    }

    if (config.getLogPlayerAdvancement()) {
      manager.registerEvents(playerListeners.new PlayerAdvancementListener(), this);
    }

    if (config.getLogPlayerCommands()) {
      manager.registerEvents(playerListeners.new PlayerCommandListener(), this);
    }

    if (config.getLogPlayerDeath()) {
      manager.registerEvents(playerListeners.new PlayerDeathListener(), this);
    }
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
