package com.hidethemonkey.elfim.command;

import com.hidethemonkey.elfim.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Locale;
import java.util.Set;

public class CommandELFS implements CommandExecutor {

    private Config config;
    private Set configKeys;
    final private String enableCMD = "enable";
    final private String disableCMD = "disable";
    final private String setCMD = "set";
    final private String tokenSubCMD = "token";
    final private String channelSubCMD = "channel";

    private final String saveMessage = "Setting updated, but will not become active until the server is restarted.";

    public CommandELFS(Config config) {
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

        if(!sender.hasPermission("elfsadmin")) {
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
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Unable to set value for [" + key + "]");
                return false;
            }
        }

        if (command.equals(setCMD)) {
            boolean validSlackSubCommands = key.equalsIgnoreCase(tokenSubCMD) || key.equalsIgnoreCase(channelSubCMD);
            boolean validURLSubCommands = key.equalsIgnoreCase(Config.avatarUrlKey) || key.equalsIgnoreCase(Config.bustUrlKey);
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
