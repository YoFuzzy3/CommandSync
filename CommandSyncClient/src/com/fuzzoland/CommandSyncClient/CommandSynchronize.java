package com.fuzzoland.CommandSyncClient;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandSynchronize implements CommandExecutor {

	private CSC plugin;
	
	public CommandSynchronize(CSC plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("sync.use")) {
			if(args.length >= 0) {
				if(args.length <= 2) {
					sender.sendMessage(ChatColor.BLUE + "CommandSync by YoFuzzy3");
					if(args.length >= 1) {
						if(args[0].equalsIgnoreCase("console")){
							sender.sendMessage(ChatColor.GREEN + "/sync console single <command+args> <server>");
							sender.sendMessage(ChatColor.GREEN + "/sync console all <command+args>");
							sender.sendMessage(ChatColor.GREEN + "/sync console bungee <command+args>");
						} else if(args[0].equalsIgnoreCase("player")) {
							sender.sendMessage(ChatColor.GREEN + "/sync player single <command+args> <player>");
							sender.sendMessage(ChatColor.GREEN + "/sync player all <command+args>");
						} else {
							sender.sendMessage(ChatColor.RED + "Type /sync for help.");
						}
					} else {
						sender.sendMessage(ChatColor.GREEN + "/sync console");
						sender.sendMessage(ChatColor.GREEN + "/sync player");
						sender.sendMessage(ChatColor.BLUE + "Type the command for more info.");
					}
					sender.sendMessage(ChatColor.BLUE + "Visit www.spigotmc.org/resources/commandsync.115 for help.");
				} else if(args.length >= 3) {
					if(args[0].equalsIgnoreCase("console")) {
						if(args[1].equalsIgnoreCase("single") || args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("bungee")) {
							makeData(args, sender);
						} else {
							sender.sendMessage(ChatColor.RED + "Type /sync for help!");
						}
					} else if(args[0].equalsIgnoreCase("player")) {
						if(args[1].equalsIgnoreCase("single") || args[1].equalsIgnoreCase("all")) {
							makeData(args, sender);
						} else {
							sender.sendMessage(ChatColor.RED + "Type /sync for help!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Type /sync for help!");
					}
				}
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
		}
		return true;
	}
	
	private void makeData(String[] args, CommandSender sender) {
		String data = args[0].toLowerCase() + plugin.spacer + args[1].toLowerCase() + plugin.spacer + args[2];
		String message = ChatColor.GREEN + "Syncing command /" + args[2].replaceAll("\\+", " ") + " to " + args[0];
		if(args.length == 4) {
			data = data + plugin.spacer + args[3];
			message = message + " [" + args[3] + "]...";
		} else {
			message = message + " [" + WordUtils.capitalizeFully(args[1]) + "]...";
		}
		plugin.oq.add(data);
		sender.sendMessage(message);
	}
}
