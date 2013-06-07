package com.fuzzoland.CommandSyncBungee;

import java.util.logging.Logger;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin{

	public Logger logger = ProxyServer.getInstance().getLogger();
	
	public void onEnable(){
		getProxy().registerChannel("CommandSync");
		getProxy().getPluginManager().registerListener(this, new EventListener(this));
	}
}
