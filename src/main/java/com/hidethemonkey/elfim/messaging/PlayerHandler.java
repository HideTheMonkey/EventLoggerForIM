package com.hidethemonkey.elfim.messaging;

import com.hidethemonkey.elfim.AdvancementConfig;
import com.hidethemonkey.elfim.Config;
import com.slack.api.model.block.LayoutBlock;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class PlayerHandler extends MessageHandler {

  /**
   * @param event
   * @param config
   */
  public static void unsuccessfulLogin(AsyncPlayerPreLoginEvent event, Config config) {
    String message = "*" + event.getName() + "* attempted to log in.\n" + event.getKickMessage();
    postMessage(message, config.getChannelId(), config.getToken());
  }

  /**
   * @param location
   * @return
   */
  private static String getLocationString(Location location) {
    return MessageFormat.format(
        "{0}, {1}, {2}",
        String.format("%.0f", location.getX()),
        String.format("%.0f", location.getY()),
        String.format("%.0f", location.getZ()));
  }

  /**
   * @param player
   * @param config
   */
  public static void playerJoin(Player player, Config config) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Player Joined");
    blocks.add(
        BlockBuilder.getSectionWithFields(
            BlockBuilder.getImageElement(
                config.getBustUrl(player.getUniqueId().toString()), player.getName()),
            BlockBuilder.getMarkdown("*NAME:* " + player.getName()),
            BlockBuilder.getMarkdown("*ADDRESS:* " + player.getAddress().getHostName()),
            BlockBuilder.getMarkdown("*OP:* " + player.isOp()),
            BlockBuilder.getMarkdown(
                "*ONLINE PLAYERS:* " + player.getServer().getOnlinePlayers().size()),
            BlockBuilder.getMarkdown("*EXP:* " + player.getTotalExperience()),
            BlockBuilder.getMarkdown("*LEVEL:* " + player.getLevel()),
            BlockBuilder.getMarkdown("*WORLD:* " + player.getWorld().getName()),
            BlockBuilder.getMarkdown("*XYZ:* " + getLocationString(player.getLocation()))));

    String message = player.getName() + " joined the server.";
    postBlocks(blocks, message, config.getChannelId(), config.getToken());
  }

  /**
   * @param player
   * @param config
   */
  public static void playerLeave(Player player, Config config) {
    long count = player.getServer().getOnlinePlayers().size() - 1;
    String message =
        "*"
            + player.getName()
            + "* left the server! Online count: *"
            + count
            + "/"
            + player.getServer().getMaxPlayers()
            + "*";
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Player Left");
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getImageElement(
                config.getAvatarUrl(player.getUniqueId().toString()), player.getName()),
            BlockBuilder.getMarkdown(message)));

    postBlocks(blocks, message, config.getChannelId(), config.getToken());
  }

  /**
   * @param event
   * @param config
   */
  public static void playerChat(AsyncPlayerChatEvent event, Config config) {
    Player player = event.getPlayer();
    String message = "*" + player.getName() + "* said: " + event.getMessage();
    List<LayoutBlock> blocks = new ArrayList<LayoutBlock>();
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getImageElement(
                config.getAvatarUrl(player.getUniqueId().toString()), player.getName()),
            BlockBuilder.getMarkdown(message)));

    postBlocks(blocks, message, config.getChannelId(), config.getToken());
  }

  /**
   * @param event
   * @param config
   */
  public static void playerCommand(PlayerCommandPreprocessEvent event, Config config) {
    String message =
        "*" + event.getPlayer().getName() + "* issued command: `" + event.getMessage() + "`";
    postMessage(message, config.getChannelId(), config.getToken());
  }


  /**
   * 
   * @param event
   * @param config
   * @param advConfig
   */
  public static void playerAdvancement(
      PlayerAdvancementDoneEvent event, Config config, AdvancementConfig advConfig) {
    if (!event.getAdvancement().getKey().getKey().startsWith("recipes")) {
      String message =
          "*"
              + event.getPlayer().getName()
              + "* has made the advancement `"
              + advConfig.getAdvancementTitle(event.getAdvancement().getKey().getKey().toString())
              + "`";
      postMessage(message, config.getChannelId(), config.getToken());
    }
  }


  public static void playerDeath(PlayerDeathEvent event, Config config) {
    List<LayoutBlock> blocks = BlockBuilder.getListBlocksWithHeader("Player Died");
    Player player = event.getEntity();
    String deathMessage = event.getDeathMessage();
    blocks.add(
        BlockBuilder.getContextBlock(
            BlockBuilder.getImageElement(
                config.getAvatarUrl(player.getUniqueId().toString()), player.getName()),
            BlockBuilder.getMarkdown(deathMessage)));

    postBlocks(blocks, deathMessage, config.getChannelId(), config.getToken());
  }
}
