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
import com.slack.api.model.block.ContextBlockElement;
import com.slack.api.model.block.LayoutBlock;
import org.bukkit.Server;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class ServerHandler extends MessageHandler {
  /**
   * @param server
   * @param config
   */
  public static void startup(Server server, ELConfig config) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Server Started");
    String pluginVersion = server.getPluginManager().getPlugin(config.getPluginName()).getDescription().getVersion();
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getMarkdown("*MOTD:* " + server.getMotd()),
            BlockBuilder.getMarkdown("*TYPE:* " + server.getName()),
            BlockBuilder.getMarkdown("*VERSION:* " + server.getVersion()),
            BlockBuilder.getMarkdown("*MAX PLAYERS:* " + server.getMaxPlayers()),
            BlockBuilder.getMarkdown("*GAME MODE:* " + server.getDefaultGameMode()),
            BlockBuilder.getMarkdown("*PLUGIN VERSION:* " + pluginVersion)));

    String serverName = server.getMotd().isBlank() ? server.getName() : server.getMotd();
    String message = "Minecraft server `" + serverName + "` is now online.";
    postBlocks(blocks, message, config.getChannelId(), config.getToken());
  }

  /**
   * @param server
   * @param config
   */
  public static void shutdown(Server server, ELConfig config) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Server Stopping");
    String pluginVersion = server.getPluginManager().getPlugin(config.getPluginName()).getDescription().getVersion();
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getMarkdown("*MOTD:* " + server.getMotd()),
            BlockBuilder.getMarkdown("*TYPE:* " + server.getName()),
            BlockBuilder.getMarkdown("*VERSION:* " + server.getVersion()),
            BlockBuilder.getMarkdown("*ONLINE PLAYERS:* " + server.getOnlinePlayers().size()),
            BlockBuilder.getMarkdown("*GAME MODE:* " + server.getDefaultGameMode()),
            BlockBuilder.getMarkdown("*PLUGIN VERSION:* " + pluginVersion)));

    String serverName = server.getMotd().isBlank() ? server.getName() : server.getMotd();
    String message =
        "`"
            + serverName
            + "` is shutting down! Players still online: *"
            + server.getOnlinePlayers().size()
            + "*";
    postBlocks(blocks, message, config.getChannelId(), config.getToken());
  }

  /**
   *
   * @param manager
   * @param config
   */
  public static void listPlugins(PluginManager manager, ELConfig config) {
    Plugin[] plugins = manager.getPlugins();
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Installed Plugins");
    ArrayList<ContextBlockElement> list = new ArrayList<>();
    for (Plugin plugin : plugins) {
      list.add(BlockBuilder.getMarkdown("*" + plugin.getName() + ":* " + plugin.getDescription().getVersion()));
    }
    blocks.add(BlockBuilder.getContextBlock(list));
    postBlocks(blocks, "List of plugins", config.getChannelId(), config.getToken());
  }

  /**
   * @param event
   * @param config
   */
  public static void serverCommand(ServerCommandEvent event, ELConfig config) {
    String message =
        "*" + event.getSender().getName() + "* issued server command: `" + event.getCommand() + "`";
    postMessage(message, config.getChannelId(), config.getToken());
  }

  /**
   * @param v
   * @return
   */
  private static String unescapeString(String v) {
    /* Replace color code &color; */
    v = v.replace("\u00A7" + "2", "");
    v = v.replace("\u00A7" + "f", "");

    return v;
  }

  /**
   * @param event
   * @param config
   */
  public static void broadcastChat(BroadcastMessageEvent event, ELConfig config) {
    String message = "*[BROADCAST]* " + unescapeString(event.getMessage());
    postMessage(message, config.getChannelId(), config.getToken());
  }
}
