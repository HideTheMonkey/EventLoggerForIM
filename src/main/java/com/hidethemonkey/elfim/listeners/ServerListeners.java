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
package com.hidethemonkey.elfim.listeners;

import com.hidethemonkey.elfim.ELConfig;
import com.hidethemonkey.elfim.messaging.ServerHandlerInterface;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;

public class ServerListeners {

  private final ELConfig config;
  private ServerHandlerInterface serverHandler;
  private final Plugin plugin;

  /**
   * @param elConfig
   * @param handler
   * @param plugin
   */
  public ServerListeners(ELConfig elConfig, ServerHandlerInterface handler, Plugin plugin) {
    config = elConfig;
    this.serverHandler = handler;
    this.plugin = plugin;
  }

  public class PluginEnableDisableListener implements Listener {
    /**
     * @param event
     */
    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
      if ((event.getPlugin().getName().equals(plugin.getName()))) {
        serverHandler.startup(event.getPlugin().getServer(), config, config.getLogStartupPlugins(serverHandler.getServiceName()));
      }
    }

    /**
     * @param event
     */
    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
      if (event.getPlugin().getName().equals(plugin.getName())) {
        serverHandler.shutdown(event.getPlugin().getServer(), config);
      }
    }
  }

  public class BroadcastMessageListener implements Listener {
    /**
     * @param event
     */
    @EventHandler
    public void onBroadcastMessage(BroadcastMessageEvent event) {
      serverHandler.broadcastChat(event, config, plugin.getLogger());
    }
  }

  public class ServerCommandListener implements Listener {
    /**
     * @param event
     */
    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
      serverHandler.serverCommand(event, config, plugin.getLogger());
    }
  }
}
