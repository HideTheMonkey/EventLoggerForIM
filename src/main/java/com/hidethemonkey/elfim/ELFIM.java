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
import com.hidethemonkey.elfim.helpers.VersionChecker;
import com.hidethemonkey.elfim.helpers.VersionData;
import com.hidethemonkey.elfim.listeners.PlayerListeners;
import com.hidethemonkey.elfim.listeners.ServerListeners;
import com.hidethemonkey.elfim.messaging.*;
import com.hidethemonkey.elfim.messaging.json.DiscordMessageFactory;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ELFIM extends JavaPlugin {

  private DiscordMessageFactory messageFactory;
  private Metrics metrics;
  private VersionData versionData;

  /**
     * 
     */
  @Override
  public void onLoad() {
    versionData = VersionChecker.getLatestReleaseVersion();
  }

  /**
   *
   */
  @Override
  public void onEnable() {
    ELConfig.updateConfig(this);
    saveDefaultConfig();

    ELConfig elConfig = new ELConfig(this.getConfig(), getLogger());

    // Store name on config for easy access later (not saved to file)
    elConfig.setPluginName(this.getName());

    // Check for new versions
    compareVersions();

    // Initialize bStats metrics
    setupMetrics(elConfig);

    saveResource("advancements.yml", true);
    AdvancementConfig advConfig = new AdvancementConfig(getDataFolder());

    // SLACK
    if (elConfig.getSlackEnabled()) {
      if (checkSlackToken(elConfig.getSlackAPIToken()) && checkSlackChannel(elConfig.getSlackChannelId())) {
        registerServerListeners(ELConfig.SLACK, elConfig, getServer().getPluginManager());
        registerPlayerListeners(ELConfig.SLACK, elConfig, advConfig, getServer().getPluginManager());
      } else {
        getLogger().warning("The Slack API token or channel is not configured!");
      }
    } else {
      getLogger().info("Slack integration is not enabled.");
    }

    // DISCORD
    if (elConfig.getDiscordEnabled()) {
      this.messageFactory = new DiscordMessageFactory(elConfig, this.getLogger());
      registerServerListeners(ELConfig.DISCORD, elConfig, getServer().getPluginManager());
      registerPlayerListeners(ELConfig.DISCORD, elConfig, advConfig, getServer().getPluginManager());
    } else {
      getLogger().info("Discord integration is not enabled.");
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
   * 
   * @return bstats metrics object
   */
  public Metrics getMetrics() {
    return metrics;
  }

  /**
   * 
   * @param config
   */
  private void setupMetrics(ELConfig config) {
    if (metrics == null) {
      // Init bStats if it's enabled
      if (config.getEnableStats()) {
        // Please don't change the ID. This helps me keep track of generic usage data.
        // The uploaded stats do not include any private information.
        this.metrics = new Metrics(this, 20980);

        // track system language
        this.metrics.addCustomChart(new SimplePie("system_language", () -> {
          return System.getProperty("user.language") + "_" + System.getProperty("user.country").toUpperCase();
        }));

        // track enabled platforms
        this.metrics.addCustomChart(new SimplePie("enabled_platforms", () -> {
          final boolean slackEnabled = config.getSlackEnabled();
          final boolean discordEnabled = config.getDiscordEnabled();
          if (slackEnabled && discordEnabled)
            return "Both";
          else if (slackEnabled)
            return "Slack";
          else if (discordEnabled)
            return "Discord";
          return "None";
        }));

      } else {
        getLogger()
            .info(
                "bStats is not enabled! Please consider activating this service to help me keep track of ELFIM usage. 🙇");
      }
    }
  }

  /**
   * @param service
   * @param config
   * @param manager
   */
  private void registerServerListeners(String service, ELConfig config, PluginManager manager) {
    ServerHandlerInterface handler = getServerHandler(service);
    ServerListeners serverListeners = new ServerListeners(config, handler, this);
    if (config.getLogServerStartStop(service)) {
      manager.registerEvents(serverListeners.new PluginEnableDisableListener(), this);
    }

    if (config.getLogBroadcasts(service)) {
      manager.registerEvents(serverListeners.new BroadcastMessageListener(), this);
    }

    if (config.getLogServerCommand(service)) {
      manager.registerEvents(serverListeners.new ServerCommandListener(), this);
    }
  }

  /**
   * @param service
   * @param config
   * @param advConfig
   * @param manager
   */
  private void registerPlayerListeners(String service, ELConfig config, AdvancementConfig advConfig,
      PluginManager manager) {
    PlayerHandlerInterface handler = getPlayerHandler(service);
    PlayerListeners playerListeners = new PlayerListeners(config, advConfig, handler, this);
    if (config.getLogPlayerJoinLeave(service)) {
      manager.registerEvents(playerListeners.new PlayerJoinListener(), this);
      manager.registerEvents(playerListeners.new PlayerQuitListener(), this);
    }

    if (config.getLogUnsuccessfulLogin(service)) {
      manager.registerEvents(playerListeners.new PlayerLoginListener(), this);
    }

    if (config.getLogChat(service)) {
      manager.registerEvents(playerListeners.new AsyncPlayerChatListener(), this);
    }

    if (config.getLogPlayerAdvancement(service)) {
      manager.registerEvents(playerListeners.new PlayerAdvancementListener(), this);
    }

    if (config.getLogPlayerCommands(service)) {
      manager.registerEvents(playerListeners.new PlayerCommandListener(), this);
    }

    if (config.getLogPlayerDeath(service)) {
      manager.registerEvents(playerListeners.new PlayerDeathListener(), this);
    }

    if (config.getLogPlayerRespawn(service)) {
      manager.registerEvents(playerListeners.new PlayerRespawnListener(), this);
    }

    if (config.getLogPlayerTeleport(service)) {
      manager.registerEvents(playerListeners.new PlayerTeleportListener(), this);
    }
  }

  /**
   * @param channelId
   * @return
   */
  private boolean checkSlackChannel(String channelId) {
    if (channelId.isBlank() || channelId.equals(ELConfig.REPLACE_ME)) {
      getLogger().severe("The Slack channel must be set in /plugin/EventLoggerForIM/config.yml!");
      return false;
    }
    return true;
  }

  /**
   * @param token
   * @return
   */
  private boolean checkSlackToken(String token) {
    if (token.isBlank() || token.equals(ELConfig.REPLACE_ME)) {
      getLogger().severe("The Slack API token must be set in /plugin/EventLoggerForIM/config.yml!");
      return false;
    }
    return true;
  }

  /**
   * @param service
   * @return
   */
  private ServerHandlerInterface getServerHandler(String service) {
    if (service.equals(ELConfig.SLACK)) {
      return new SlackServerHandler();
    }
    if (service.equals(ELConfig.DISCORD)) {
      return new DiscordServerHandler(messageFactory);
    }
    return null;
  }

  /**
   * @param service
   * @return
   */
  private PlayerHandlerInterface getPlayerHandler(String service) {
    if (service.equals(ELConfig.SLACK)) {
      return new SlackPlayerHandler();
    }
    if (service.equals(ELConfig.DISCORD)) {
      return new DiscordPlayerHandler(messageFactory);
    }
    return null;
  }

  /**
  * 
  */
  private void compareVersions() {
    if (versionData == null) {
      getLogger().warning(
          "Could not check for new versions. Please see https://hangar.papermc.io/HideTheMonkey/EventLoggerForIM for updates.");
      return;
    }
    DefaultArtifactVersion latestVersion = new DefaultArtifactVersion(versionData.getVersion());
    DefaultArtifactVersion currentVersion = new DefaultArtifactVersion(getDescription().getVersion());
    if (latestVersion.compareTo(currentVersion) > 0) {
      getLogger().warning("****************************************************************************");
      getLogger().warning("* A new release of EventLoggerForIM is available!");
      getLogger().warning("*");
      getLogger().warning("* New version: " + versionData.getVersion());
      getLogger().warning("* Your version: " + getDescription().getVersion());
      getLogger().warning("*");
      getLogger().warning("* Please update to take advantage of the latest features and bug fixes.");
      getLogger().warning("* Download here: https://hangar.papermc.io/HideTheMonkey/EventLoggerForIM");
      getLogger().warning("****************************************************************************");
    }
  }
}
