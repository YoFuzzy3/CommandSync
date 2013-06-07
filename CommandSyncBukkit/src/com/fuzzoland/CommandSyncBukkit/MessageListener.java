package com.fuzzoland.CommandSyncBukkit;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class MessageListener implements PluginMessageListener{
	
	private Main plugin;
	
	public MessageListener(Main plugin){
		this.plugin = plugin;
	}
	
	public void onPluginMessageReceived(String channel, Player player, byte[] data){
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
		try{
			String command = in.readUTF();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			plugin.logger.log(Level.INFO, "[CommandSync] Ran command: " + command);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
