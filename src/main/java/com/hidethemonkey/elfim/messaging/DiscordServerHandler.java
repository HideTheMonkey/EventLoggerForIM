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
package com.hidethemonkey.elfim.messaging;

import com.google.gson.Gson;
import com.hidethemonkey.elfim.ELConfig;
import com.hidethemonkey.elfim.helpers.Localizer;
import com.hidethemonkey.elfim.helpers.NetworkUtils;
import com.hidethemonkey.elfim.helpers.StringUtils;
import com.hidethemonkey.elfim.messaging.json.DiscordMessage;
import com.hidethemonkey.elfim.messaging.json.DiscordMessageFactory;
import com.hidethemonkey.elfim.messaging.json.Embed;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;

import net.kyori.adventure.text.TextComponent;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class DiscordServerHandler extends MessageHandler implements ServerHandlerInterface {

  private final Gson gson = new Gson();
  private DiscordMessageFactory messageFactory;

  public DiscordServerHandler(DiscordMessageFactory messageFactory, Localizer localizer) {
    super(localizer);
    this.messageFactory = messageFactory;
  }

  /**
   * @return
   */
  @Override
  public String getServiceName() {
    return ELConfig.DISCORD;
  }

  /**
   * @param server
   * @param config
   * @param logPlugins
   */
  @Override
  public void startup(Server server, ELConfig config, boolean logPlugins) {
    Plugin plugin = server.getPluginManager().getPlugin(config.getPluginName());
    String pluginVersion = plugin.getPluginMeta().getVersion();
    String updateAvailable = ELConfig.getUpdateAvailable()
        ? " ([" + localizer.t("update_available")
            + "](https://github.com/HideTheMonkey/EventLoggerForIM/releases/latest))"
        : "";
    Embed embed = new Embed(config.getDiscordColor("serverStartup"));
    embed.setTitle(String.format("**%s**", localizer.t("server_started")));
    embed.addField(localizer.t("motd"), ((TextComponent) server.motd()).content());
    embed.addField(localizer.t("type"), server.getName());
    embed.addField(localizer.t("version"), server.getVersion());
    embed.addField(localizer.t("max_players"), Integer.toString(server.getMaxPlayers()));
    embed.addField(localizer.t("game_mode"), server.getDefaultGameMode().toString());
    embed.addField(localizer.t("local_ip"), NetworkUtils.getLocalIP(server.getIp()));
    embed.addField(localizer.t("external_ip"), NetworkUtils.getExternalIP());
    embed.addField(localizer.t("elfim_version"), pluginVersion + updateAvailable);

    DiscordMessage message = messageFactory.getMessage(embed);
    // delay task to ensure the plugins are fully loaded so we get an accurate state
    Bukkit.getScheduler().runTaskLater(server.getPluginManager().getPlugin(config.getPluginName()), task -> {
      if (logPlugins) {
        Embed pluginEmbed = new Embed(config.getDiscordColor("serverPlugins"));
        pluginEmbed.setTitle(String.format("**%s**", localizer.t("installed_plugins")));
        Plugin[] plugins = server.getPluginManager().getPlugins();
        for (Plugin installedPlugin : plugins) {
          String disabledString = installedPlugin.isEnabled() ? "" : String.format(" [_%s_]", localizer.t("disabled"));
          pluginEmbed.addField(installedPlugin.getName() + disabledString,
              installedPlugin.getPluginMeta().getVersion());
        }
        message.addEmbed(pluginEmbed);
      }
      List<String> logProperties = config.getLogProperties();
      if (logProperties.size() > 0) {
        Embed propertiesEmbed = new Embed(config.getDiscordColor("serverProperties"));
        propertiesEmbed.setTitle(String.format("**%s**", localizer.t("server_properties")));
        Properties props = new Properties();
        try {
          props.load(new FileInputStream("server.properties"));
          for (String key : logProperties) {
            String value = props.getProperty(key);
            if (value == null) {
              value = "";
            }
            propertiesEmbed.addField(key, value);
          }
        } catch (Exception e) {
          propertiesEmbed.addField(localizer.t("error"), e.getLocalizedMessage());
          e.printStackTrace();
        }

        message.addEmbed(propertiesEmbed);
      }

      postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), plugin.getLogger());
    }, 1);
  }

  /**
   * @param server
   * @param config
   */
  @Override
  public void shutdown(Server server, ELConfig config) {
    Plugin plugin = server.getPluginManager().getPlugin(config.getPluginName());
    String pluginVersion = plugin.getPluginMeta().getVersion();
    String updateAvailable = ELConfig.getUpdateAvailable()
        ? " ([" + localizer.t("update_available")
            + "](https://github.com/HideTheMonkey/EventLoggerForIM/releases/latest))"
        : "";
    Embed embed = new Embed(config.getDiscordColor("serverShutdown"));
    embed.setTitle("**Server Stopping**");
    embed.addField(localizer.t("motd"), ((TextComponent) server.motd()).content());
    embed.addField(localizer.t("type"), server.getName());
    embed.addField(localizer.t("version"), server.getVersion());
    embed.addField(localizer.t("online_players"), Integer.toString(server.getOnlinePlayers().size()));
    embed.addField(localizer.t("game_mode"), server.getDefaultGameMode().toString());
    embed.addField(localizer.t("elfim_version"), pluginVersion + updateAvailable);
    DiscordMessage message = messageFactory.getMessage(embed);

    postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), plugin.getLogger());
  }

  /**
   * @param event
   * @param config
   * @param logger
   */
  @Override
  public void serverCommand(ServerCommandEvent event, ELConfig config, Logger logger) {
    Embed embed = new Embed(config.getDiscordColor("serverCommand"));
    embed.setTitle(localizer.t("server_command"));
    embed.setDescription(localizer.t("server.issued_command", event.getSender().getName(), event.getCommand()));
    DiscordMessage message = messageFactory.getMessage(embed);

    postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), logger);
  }

  /**
   * @param event
   * @param config
   * @param logger
   */
  @Override
  public void broadcastChat(BroadcastMessageEvent event, ELConfig config, Logger logger) {
    Embed embed = new Embed(config.getDiscordColor("serverBroadcast"));
    embed.setTitle(localizer.t("server.broadcast_message"));
    embed.setDescription(StringUtils.removeSpecialChars(((TextComponent) event.message()).content()));
    DiscordMessage message = messageFactory.getMessage(embed);

    postWebhook(config.getDiscordWebhookUrl(), gson.toJson(message), logger);
  }
}
