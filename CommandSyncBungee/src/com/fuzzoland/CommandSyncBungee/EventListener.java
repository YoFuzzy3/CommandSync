package com.fuzzoland.CommandSyncBungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class EventListener implements Listener{

	private Main plugin;
	
	public EventListener(Main plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPluginMessage(PluginMessageEvent event) throws IOException{
		if(event.getTag().equals("CommandSync")){
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
			String channel = in.readUTF();
			if(channel.equals("Console")){
				String type = in.readUTF();
				if(type.equals("All")){
					String command = in.readUTF();
					for(Entry<String, ServerInfo> entrySet : ProxyServer.getInstance().getServers().entrySet()){
						ByteArrayOutputStream b = new ByteArrayOutputStream();
					    DataOutputStream out = new DataOutputStream(b);
						out.writeUTF(command);
						ProxyServer.getInstance().getScheduler().runAsync(plugin, new PluginMessageTask(entrySet.getValue(), "CommandSync", b));
					}
					plugin.logger.log(Level.INFO, "[CommandSync] Sent command " + command + " to all servers.");
				}else if(type.equals("Single")){
					String serverName = in.readUTF();
					ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);
					if(server != null){
						String command = in.readUTF();
						ByteArrayOutputStream b = new ByteArrayOutputStream();
					    DataOutputStream out = new DataOutputStream(b);
						out.writeUTF(command);
						ProxyServer.getInstance().getScheduler().runAsync(plugin, new PluginMessageTask(server, "CommandSync", b));
						plugin.logger.log(Level.INFO, "[CommandSync] Sent command " + command + " to server " + server.getName() + ".");
					}
				}
			}else if(channel.equals("Player")){
				String type = in.readUTF();
				if(type.equals("All")){
					String command = in.readUTF();
					for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
						player.chat(command);
					}
					plugin.logger.log(Level.INFO, "[CommandSync] Ran command " + command + " for all players.");
				}else if(type.equals("Single")){
					String playerName = in.readUTF();
					ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
					if(player != null){
						String command = in.readUTF();
						player.chat(command);
						plugin.logger.log(Level.INFO, "[CommandSync] Ran command " + command + " for " + player.getName() + ".");
					}
				}
			}
		}
	}
}
