package com.fuzzoland.CommandSyncBungee;

import java.io.ByteArrayOutputStream;

import net.md_5.bungee.api.config.ServerInfo;

public class PluginMessageTask implements Runnable{

	private String channel = null;
	private ByteArrayOutputStream bytes = null;
	private ServerInfo server = null;

	public PluginMessageTask(ServerInfo server, String channel, ByteArrayOutputStream bytes){
		this.server = server;
		this.channel = channel;
		this.bytes = bytes;
	}

	public void run(){
		this.server.sendData(this.channel, this.bytes.toByteArray());
	}
}
