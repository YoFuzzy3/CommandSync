package com.fuzzoland.CommandSyncBukkit;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{

	public Logger logger = Bukkit.getLogger();
	
	public void onEnable(){
		getServer().getMessenger().registerIncomingPluginChannel(this, "CommandSync", new MessageListener(this));
		getServer().getMessenger().registerOutgoingPluginChannel(this, "CommandSync");
		getCommand("Sync").setExecutor(new CommandSync(this));
	}
}
