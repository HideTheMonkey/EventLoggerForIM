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
package com.hidethemonkey.elfim.commands;

import com.hidethemonkey.elfim.ELConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Locale;
import java.util.Set;

public class CommandELFS implements CommandExecutor {

    private ELConfig config;
    private Set configKeys;
    final private String enableCMD = "enable";
    final private String disableCMD = "disable";
    final private String setCMD = "set";
    final private String tokenSubCMD = "token";
    final private String channelSubCMD = "channel";

    private final String saveMessage = "Setting updated, but will not become active until the server is restarted.";

    public CommandELFS(ELConfig config) {
        this.config = config;
        this.configKeys = config.getKeys();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
        if (args.length < 2) {
            return false;
        }
        String command = args[0].toLowerCase(Locale.ROOT);
        String key = args[1];
        String value = args.length == 3 ? args[2] : "";

        if(!sender.hasPermission("ELFIM.elfimadmin")) {
            sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to run this command.");
            return false;
        }

        if (command.isBlank() || !(command.equals(enableCMD) || command.equals(disableCMD) || command.equals(setCMD))) {
            sender.sendMessage(ChatColor.DARK_RED + "Command [" + command + "] must be either enable, disable, or set");
            return false;
        }

        if(command.equals(enableCMD) || command.equals(disableCMD)) {
            if (configKeys.contains("slack.events." + key)) {
                config.setBoolean("slack.events." + key, command.equals(enableCMD));
                sender.sendMessage(ChatColor.GOLD + saveMessage);
            } else if (key.equals("slack") || key.equals("discord")) {
                config.setBoolean("enable" + key.substring(0, 1).toUpperCase(Locale.ROOT) + key.substring(1), command.equals(enableCMD));
                sender.sendMessage(ChatColor.GOLD + saveMessage);
            }
            else {
                sender.sendMessage(ChatColor.DARK_RED + "Unable to set value for [" + key + "]");
                return false;
            }
        }

        if (command.equals(setCMD)) {
            boolean validSlackSubCommands = key.equalsIgnoreCase(tokenSubCMD) || key.equalsIgnoreCase(channelSubCMD);
            boolean validURLSubCommands = key.equalsIgnoreCase(ELConfig.avatarUrlKey) || key.equalsIgnoreCase(ELConfig.bustUrlKey);
            if (validSlackSubCommands) {
                String slackKey = key.equalsIgnoreCase(tokenSubCMD) ? "slack.apiToken" : "slack.channelId";
                config.setString(slackKey, value);
                sender.sendMessage(ChatColor.GOLD + saveMessage);
            } else if (validURLSubCommands) {
                config.setString(key, value);
                sender.sendMessage(ChatColor.GOLD + saveMessage);
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Sub Command [" + key + "] is not a valid option.");
                return false;
            }
        }

        return true;
    }
}
