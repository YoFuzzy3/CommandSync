package com.fuzzoland.CommandSyncBukkit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandSync implements CommandExecutor{

	private Main plugin;
	
	public CommandSync(Main plugin){
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender.hasPermission("CommandSync.use")){
			if(args.length == 0){
				sender.sendMessage(ChatColor.BLUE + "CommandSync v" + plugin.getDescription().getVersion() + " by YoFuzzy3");
				sender.sendMessage(ChatColor.GREEN + "/Sync console <server-name/all> </your-command_args>");
				sender.sendMessage(ChatColor.GREEN + "/Sync player <player-name/all> </your-command_args>");
				sender.sendMessage(ChatColor.BLUE + "Visit www.spigotmc.org/resources/commandsync.115 for help.");
			}else if(args.length == 3){
				try{
					ByteArrayOutputStream b = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream(b);
					String command = args[2].replaceAll("_", " ");
					if(args[0].equalsIgnoreCase("console")){
						out.writeUTF("Console");
						if(args[1].equalsIgnoreCase("all")){
							out.writeUTF("All");
							out.writeUTF(command.replaceFirst("/", ""));
							sender.sendMessage(ChatColor.GREEN + "Successfully ran command " + command + " for all servers.");
						}else{
							out.writeUTF("Single");
							out.writeUTF(args[1]);
							out.writeUTF(command.replaceFirst("/", ""));
							sender.sendMessage(ChatColor.GREEN + "Successfully ran command " + command + " for server " + args[1] + ".");
						}
						Bukkit.getScheduler().runTaskAsynchronously(plugin, new PluginMessageTask(plugin, "CommandSync", b));
						plugin.logger.log(Level.INFO, "[CommandSync] Sent data to BungeeCord.");
					}else if(args[0].equalsIgnoreCase("player")){
						out.writeUTF("Player");
						if(args[1].equalsIgnoreCase("all")){
							out.writeUTF("All");
							out.writeUTF(command);
							sender.sendMessage(ChatColor.GREEN + "Successfully ran command " + command + " for all players.");
						}else{
							out.writeUTF("Single");
							out.writeUTF(args[1]);
							out.writeUTF(command);
							sender.sendMessage(ChatColor.GREEN + "Successfully ran command " + command + " for player " + args[1] + ".");
						}
						Bukkit.getScheduler().runTaskAsynchronously(plugin, new PluginMessageTask(plugin, "CommandSync", b));
						plugin.logger.log(Level.INFO, "[CommandSync] Sent command " + command + " to BungeeCord.");
					}else{
						sender.sendMessage(ChatColor.RED + "Type /Sync for help!");
					}
					b.close();
					out.close();
				}catch(IOException e){
					e.printStackTrace();
					sender.sendMessage(ChatColor.RED + "An error occurred, check the console.");
				}
			}else{
				sender.sendMessage(ChatColor.RED + "Type /Sync for help!");
			}
		}else{
			sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
		}
		return true;
	}
}
