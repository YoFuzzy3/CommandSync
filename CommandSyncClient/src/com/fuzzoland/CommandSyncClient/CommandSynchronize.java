package com.fuzzoland.CommandSyncClient;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandSynchronize implements CommandExecutor{

	private CSC plugin;
	
	public CommandSynchronize(CSC plugin){
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender.hasPermission("commandsync.synchronize")){
			plugin.oq.add("test" + String.valueOf(new Random().nextInt()));
		}else{
			sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
		}
		return true;
	}
}
