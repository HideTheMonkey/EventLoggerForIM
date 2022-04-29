package com.hidethemonkey.elfim.listeners;

import com.hidethemonkey.elfim.AdvancementConfig;
import com.hidethemonkey.elfim.Config;
import com.hidethemonkey.elfim.messaging.PlayerHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class PlayerEventListeners implements Listener {
  private final Config config;
  private final AdvancementConfig advConfig;
  private boolean logChat = false;
  private boolean logPlayerAdvancement = false;
  private boolean logPlayerCommands = false;
  private boolean logPlayerDeath = false;
  private boolean logPlayerJoinLeave = false;
  private boolean logUnsuccessfulLogin = false;

  public PlayerEventListeners(Config config, AdvancementConfig advConfig) {
    this.config = config;
    this.advConfig = advConfig;
    logChat = config.getLogChat();
    logPlayerAdvancement = config.getLogPlayerAdvancement();
    logPlayerCommands = config.getLogPlayerCommands();
    logPlayerDeath = config.getLogPlayerDeath();
    logPlayerJoinLeave = config.getLogPlayerJoinLeave();
    logUnsuccessfulLogin = config.getLogUnsuccessfulLogin();
  }

  /**
   * @param event
   */
  @EventHandler
  public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
    if (logChat) {
      PlayerHandler.playerChat(event, config);
    }
  }

  /**
   * @param event
   */
  @EventHandler
  public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
    if (logPlayerCommands) {
      PlayerHandler.playerCommand(event, config);
    }
  }

  /**
   * @param event
   */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (logPlayerDeath) {
      PlayerHandler.playerDeath(event, config);
    }
  }

  /**
   * @param event
   */
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (logPlayerJoinLeave) {
      PlayerHandler.playerJoin(event.getPlayer(), config);
    }
  }

  /**
   * @param event
   */
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (logPlayerJoinLeave) {
      PlayerHandler.playerLeave(event.getPlayer(), config);
    }
  }

  /**
   * @param event
   */
  @EventHandler
  public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
    if (logPlayerAdvancement) {
      PlayerHandler.playerAdvancement(event, config, advConfig);
    }
  }

  /**
   * @param event
   */
  @EventHandler
  public void onPlayerLogin(PlayerLoginEvent event) {
    if (logUnsuccessfulLogin) {
      if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
        PlayerHandler.unsuccessfulLogin(event, config);
      }
    }
  }
}
