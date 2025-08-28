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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.hidethemonkey.elfim.ELConfig;
import com.hidethemonkey.elfim.helpers.Localizer;
import com.hidethemonkey.elfim.helpers.NetworkUtils;
import com.hidethemonkey.elfim.helpers.StringUtils;
import com.slack.api.model.block.ContextBlockElement;
import com.slack.api.model.block.LayoutBlock;

import net.kyori.adventure.text.TextComponent;

public class SlackServerHandler extends MessageHandler implements ServerHandlerInterface {

  public SlackServerHandler(Localizer localizer) {
    super(localizer);
  }

  /**
   *
   * @return
   */
  @Override
  public String getServiceName() {
    return ELConfig.SLACK;
  }

  /**
   * @param server
   * @param config
   * @param logPlugins
   */
  @Override
  public void startup(Server server, ELConfig config, boolean logPlugins) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader(localizer.t("server_started"));
    String pluginVersion = server.getPluginManager().getPlugin(config.getPluginName()).getPluginMeta().getVersion();
    String updateAvailable = ELConfig.getUpdateAvailable()
        ? " (<https://github.com/HideTheMonkey/EventLoggerForIM/releases/latest|" + localizer.t("update_available")
            + ">)"
        : "";
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder
                .getMarkdown(String.format("*%s:* %s", localizer.t("motd"), ((TextComponent) server.motd()).content())),
            BlockBuilder.getMarkdown(String.format("*%s:* %s", localizer.t("type"), server.getName())),
            BlockBuilder.getMarkdown(String.format("*%s:* %s", localizer.t("version"), server.getVersion())),
            BlockBuilder.getMarkdown(String.format("*%s:* %d", localizer.t("max_players"), server.getMaxPlayers())),
            BlockBuilder.getMarkdown(String.format("*%s:* %s", localizer.t("game_mode"), server.getDefaultGameMode())),
            BlockBuilder.getMarkdown(
                String.format("*%s:* %s", localizer.t("local_ip"), NetworkUtils.getLocalIP(server.getIp()))),
            BlockBuilder
                .getMarkdown(String.format("*%s:* %s", localizer.t("external_ip"), NetworkUtils.getExternalIP())),
            BlockBuilder
                .getMarkdown(
                    String.format("*%s:* %s", localizer.t("elfim_version"), pluginVersion + updateAvailable))));

    String serverName = ((TextComponent) server.motd()).content().isBlank() ? server.getName()
        : ((TextComponent) server.motd()).content();
    String message = localizer.t("slack.server_online", serverName);
    postBlocks(blocks, message, config.getSlackChannelId(), config.getSlackAPIToken());
    try {
      // give it some time to post before listing plugins and properties
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    if (logPlugins) {
      // delay task to ensure the plugins are fully loaded so we get an accurate state
      Bukkit.getScheduler().runTaskLater(server.getPluginManager().getPlugin(config.getPluginName()), task -> {
        listPlugins(server.getPluginManager(), config);
      }, 1);
    }
    List<String> logProperties = config.getLogProperties();
    if (logProperties.size() > 0) {
      listProperties(server.getPluginManager(), config, logProperties);
    }
  }

  /**
   * @param server
   * @param config
   */
  @Override
  public void shutdown(Server server, ELConfig config) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader(localizer.t("server_stopping"));
    String pluginVersion = server.getPluginManager().getPlugin(config.getPluginName()).getPluginMeta().getVersion();
    String updateAvailable = ELConfig.getUpdateAvailable()
        ? " (<https://github.com/HideTheMonkey/EventLoggerForIM/releases/latest|" + localizer.t("update_available")
            + ">)"
        : "";
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder
                .getMarkdown(String.format("*%s:* %s", localizer.t("motd"), ((TextComponent) server.motd()).content())),
            BlockBuilder.getMarkdown(String.format("*%s:* %s", localizer.t("type"), server.getName())),
            BlockBuilder.getMarkdown(String.format("*%s:* %s", localizer.t("version"), server.getVersion())),
            BlockBuilder
                .getMarkdown(
                    String.format("*%s:* %d", localizer.t("online_players"), server.getOnlinePlayers().size())),
            BlockBuilder.getMarkdown(String.format("*%s:* %s", localizer.t("game_mode"), server.getDefaultGameMode())),
            BlockBuilder
                .getMarkdown(
                    String.format("*%s:* %s", localizer.t("elfim_version"), pluginVersion + updateAvailable))));

    String serverName = ((TextComponent) server.motd()).content().isBlank() ? server.getName()
        : ((TextComponent) server.motd()).content();
    postBlocks(blocks, localizer.t("server_shutdown", serverName, server.getOnlinePlayers().size()),
        config.getSlackChannelId(), config.getSlackAPIToken());
  }

  /**
   * @param manager
   * @param config
   */
  private void listPlugins(PluginManager manager, ELConfig config) {
    Plugin[] plugins = manager.getPlugins();
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader(localizer.t("installed_plugins"));
    ArrayList<ContextBlockElement> list = new ArrayList<>();
    for (Plugin plugin : plugins) {
      String disabledString = plugin.isEnabled() ? ": " : String.format(" [_%s_]: ", localizer.t("disabled"));
      list.add(BlockBuilder
          .getMarkdown("*" + plugin.getName() + "*" + disabledString + plugin.getPluginMeta().getVersion()));
    }
    blocks.add(BlockBuilder.getContextBlock(list));
    postBlocks(blocks, localizer.t("list_of_plugins"), config.getSlackChannelId(), config.getSlackAPIToken());
  }

  private void listProperties(PluginManager manager, ELConfig config, List<String> properties) {
    Properties props = new Properties();
    ArrayList<ContextBlockElement> list = new ArrayList<>();
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader(localizer.t("server_properties"));

    try {
      props.load(new FileInputStream("server.properties"));
      for (String key : properties) {
        list.add(BlockBuilder.getMarkdown("*" + key + ":* " + props.getProperty(key)));
      }
    } catch (IOException e) {
      list.add(BlockBuilder.getMarkdown(localizer.t("server.error_loading_properties", e.getLocalizedMessage())));
    }

    blocks.add(BlockBuilder.getContextBlock(list));
    postBlocks(blocks, localizer.t("server.list_of_properties"), config.getSlackChannelId(), config.getSlackAPIToken());
  }

  /**
   * @param event
   * @param config
   * @param logger
   */
  @Override
  public void serverCommand(ServerCommandEvent event, ELConfig config, Logger logger) {
    postMessage(localizer.t("server.issued_command", event.getSender().getName(), event.getCommand()),
        config.getSlackChannelId(), config.getSlackAPIToken());
  }

  /**
   * @param event
   * @param config
   * @param logger
   */
  @Override
  public void broadcastChat(BroadcastMessageEvent event, ELConfig config, Logger logger) {
    String message = String.format("*[%s]* ",
        localizer.t("server.broadcast_message",
            StringUtils.removeSpecialChars(((TextComponent) event.message()).content())));
    postMessage(message, config.getSlackChannelId(), config.getSlackAPIToken());
  }
}
