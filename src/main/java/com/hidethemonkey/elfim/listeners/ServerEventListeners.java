package com.hidethemonkey.elfim.listeners;

import com.hidethemonkey.elfim.Config;
import com.hidethemonkey.elfim.messaging.ServerHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class ServerEventListeners implements Listener {

  private final Config config;

  private boolean logBroadcasts = false;
  private boolean logServerCommand = false;
  private boolean logServerStartStop = false;
  private boolean logStartupPlugins = false;

  /**
   * @param config
   */
  public ServerEventListeners(Config config) {
    this.config = config;

    logBroadcasts = config.getLogBroadcasts();
    logServerCommand = config.getLogServerCommand();
    logServerStartStop = config.getLogServerStartStop();
    logStartupPlugins = config.getLogStartupPlugins();
  }

  /**
   * @param event
   */
  @EventHandler
  public void onPluginEnable(PluginEnableEvent event) {
    if ((event.getPlugin().getName().equals(this.config.getPluginName()))) {
      if (logServerStartStop) {
        ServerHandler.startup(event.getPlugin().getServer(), config);
      }
      if (logStartupPlugins) {
        ServerHandler.listPlugins(event.getPlugin().getServer().getPluginManager(), config);
      }
    }
  }

  /**
   * @param event
   */
  @EventHandler
  public void onPluginDisable(PluginDisableEvent event) {
    if (logServerStartStop && (event.getPlugin().getName().equals(this.config.getPluginName()))) {
      ServerHandler.shutdown(event.getPlugin().getServer(), config);
    }
  }

  /**
   * @param event
   */
  @EventHandler
  public void onBroadcastMessage(BroadcastMessageEvent event) {
    if (logBroadcasts) {
      ServerHandler.broadcastChat(event, config);
    }
  }

  /**
   * @param event
   */
  @EventHandler
  public void onServerCommand(ServerCommandEvent event) {
    if (logServerCommand) {
      ServerHandler.serverCommand(event, config);
    }
  }
}
