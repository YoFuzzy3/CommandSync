package com.fuzzoland.CommandSyncServer;

import java.io.IOException;

public class ClientListener extends Thread{

	private CSS plugin;
	private Integer heartbeat;

	public ClientListener(CSS plugin, Integer heartbeat){
		this.plugin = plugin;
		this.heartbeat = heartbeat;
	}

	public void run(){
		while(true){
			try{
				new ClientHandler(plugin, plugin.server.accept(), heartbeat).start();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}
