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

import com.hidethemonkey.elfim.ELConfig;
import com.hidethemonkey.elfim.helpers.StringUtils;
import com.slack.api.model.block.ContextBlockElement;
import com.slack.api.model.block.LayoutBlock;
import org.bukkit.Server;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import net.kyori.adventure.text.TextComponent;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class SlackServerHandler extends MessageHandler implements ServerHandlerInterface {

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
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Server Started");
    String pluginVersion = server.getPluginManager().getPlugin(config.getPluginName()).getPluginMeta().getVersion();
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getMarkdown("*MOTD:* " + ((TextComponent) server.motd()).content()),
            BlockBuilder.getMarkdown("*TYPE:* " + server.getName()),
            BlockBuilder.getMarkdown("*VERSION:* " + server.getVersion()),
            BlockBuilder.getMarkdown("*MAX PLAYERS:* " + server.getMaxPlayers()),
            BlockBuilder.getMarkdown("*GAME MODE:* " + server.getDefaultGameMode()),
            BlockBuilder.getMarkdown("*ELFIM VERSION:* " + pluginVersion)));

    String serverName = ((TextComponent) server.motd()).content().isBlank() ? server.getName()
        : ((TextComponent) server.motd()).content();
    String message = "Minecraft server `" + serverName + "` is now online.";
    postBlocks(blocks, message, config.getSlackChannelId(), config.getSlackAPIToken());
    try {
      // give it some time to post before listing plugins and properties
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (logPlugins) {
      listPlugins(server.getPluginManager(), config);
    }
    List<String> logProperties = config.getLogProperties();
    if (logProperties.size() > 0) {
      listPproperties(server.getPluginManager(), config, logProperties);
    }
  }

  /**
   * @param server
   * @param config
   */
  @Override
  public void shutdown(Server server, ELConfig config) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Server Stopping");
    String pluginVersion = server.getPluginManager().getPlugin(config.getPluginName()).getPluginMeta().getVersion();
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getMarkdown("*MOTD:* " + ((TextComponent) server.motd()).content()),
            BlockBuilder.getMarkdown("*TYPE:* " + server.getName()),
            BlockBuilder.getMarkdown("*VERSION:* " + server.getVersion()),
            BlockBuilder.getMarkdown("*ONLINE PLAYERS:* " + server.getOnlinePlayers().size()),
            BlockBuilder.getMarkdown("*GAME MODE:* " + server.getDefaultGameMode()),
            BlockBuilder.getMarkdown("*ELFIM VERSION:* " + pluginVersion)));

    String serverName = ((TextComponent) server.motd()).content().isBlank() ? server.getName()
        : ((TextComponent) server.motd()).content();
    String message = "`"
        + serverName
        + "` is shutting down! Players still online: *"
        + server.getOnlinePlayers().size()
        + "*";
    postBlocks(blocks, message, config.getSlackChannelId(), config.getSlackAPIToken());
  }

  /**
   * @param manager
   * @param config
   */
  private void listPlugins(PluginManager manager, ELConfig config) {
    Plugin[] plugins = manager.getPlugins();
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Installed Plugins");
    ArrayList<ContextBlockElement> list = new ArrayList<>();
    for (Plugin plugin : plugins) {
      list.add(BlockBuilder.getMarkdown("*" + plugin.getName() + ":* " + plugin.getPluginMeta().getVersion()));
    }
    blocks.add(BlockBuilder.getContextBlock(list));
    postBlocks(blocks, "List of plugins", config.getSlackChannelId(), config.getSlackAPIToken());
  }

  private void listPproperties(PluginManager manager, ELConfig config, List<String> properties) {
    Properties props = new Properties();
    ArrayList<ContextBlockElement> list = new ArrayList<>();
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Server Properties");

    try {
      props.load(new FileInputStream("server.properties"));
      for (String key : properties) {
        list.add(BlockBuilder.getMarkdown("*" + key + ":* " + props.getProperty(key)));
      }
    } catch (Exception e) {
      list.add(BlockBuilder.getMarkdown("Error loading server.properties: " + e.getLocalizedMessage()));
      e.printStackTrace();
    }

    blocks.add(BlockBuilder.getContextBlock(list));
    postBlocks(blocks, "List of properties", config.getSlackChannelId(), config.getSlackAPIToken());
  }

  /**
   * @param event
   * @param config
   * @param logger
   */
  @Override
  public void serverCommand(ServerCommandEvent event, ELConfig config, Logger logger) {
    String message = "*" + event.getSender().getName() + "* issued server command: `" + event.getCommand() + "`";
    postMessage(message, config.getSlackChannelId(), config.getSlackAPIToken());
  }

  /**
   * @param event
   * @param config
   * @param logger
   */
  @Override
  public void broadcastChat(BroadcastMessageEvent event, ELConfig config, Logger logger) {
    String message = "*[BROADCAST]* " + StringUtils.removeSpecialChars(((TextComponent) event.message()).content());
    postMessage(message, config.getSlackChannelId(), config.getSlackAPIToken());
  }
}
