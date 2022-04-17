package com.hidethemonkey.elfim.messaging;

import com.hidethemonkey.elfim.Config;
import com.slack.api.model.block.LayoutBlock;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class ServerHandler extends MessageHandler {
  /**
   * @param server
   * @param config
   */
  public static void startup(Server server, Config config) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Server Started");
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getMarkdown("*MOTD:* " + server.getMotd()),
            BlockBuilder.getMarkdown("*TYPE:* " + server.getName()),
            BlockBuilder.getMarkdown("*VERSION:* " + server.getVersion()),
            BlockBuilder.getMarkdown("*MAX PLAYERS:* " + server.getMaxPlayers()),
            BlockBuilder.getMarkdown("*GAME MODE:* " + server.getDefaultGameMode()),
            BlockBuilder.getMarkdown("*PLUGIN VERSION:* " + config.getVersion())));

    String serverName = server.getMotd().isBlank() ? server.getName() : server.getMotd();
    String message = "Minecraft server `" + serverName + "` is now online.";
    postBlocks(blocks, message, config.getChannelId(), config.getToken());
  }

  /**
   * @param server
   * @param config
   */
  public static void shutdown(Server server, Config config) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Server Stopping");
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getMarkdown("*MOTD:* " + server.getMotd()),
            BlockBuilder.getMarkdown("*TYPE:* " + server.getName()),
            BlockBuilder.getMarkdown("*VERSION:* " + server.getVersion()),
            BlockBuilder.getMarkdown("*ONLINE PLAYERS:* " + server.getOnlinePlayers().size()),
            BlockBuilder.getMarkdown("*GAME MODE:* " + server.getDefaultGameMode()),
            BlockBuilder.getMarkdown("*PLUGIN VERSION:* " + config.getVersion())));

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
   * @param event
   * @param config
   */
  public static void serverCommand(ServerCommandEvent event, Config config) {
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
  public static void broadcastChat(BroadcastMessageEvent event, Config config) {
    String message = "*[BROADCAST]* " + unescapeString(event.getMessage());
    postMessage(message, config.getChannelId(), config.getToken());
  }
}
