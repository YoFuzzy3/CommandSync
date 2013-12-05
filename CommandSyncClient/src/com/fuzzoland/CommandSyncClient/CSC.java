package com.fuzzoland.CommandSyncClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

public class CSC extends JavaPlugin{

	public ClientThread client;
	public List<String> oq = Collections.synchronizedList(new ArrayList<String>());
	
	public void onEnable(){
		String[] data = loadConfig();
		try{
			client = new ClientThread(this, InetAddress.getByName(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
			client.start();
		}catch(Exception e){
			e.printStackTrace();
		}
		getCommand("Sync").setExecutor(new CommandSynchronize(this));
	}
	
	private String[] loadConfig(){
		String[] data = new String[2];
		try{
			File file = getDataFolder();
			if(!file.exists()){
				file.mkdirs();
				OutputStream os = new FileOutputStream(file + "/config.txt");
				PrintStream ps = new PrintStream(os);
				ps.println("ip=localhost");
				ps.println("port=9190");
				ps.println("heartbeat=5000");
				ps.println("name=null");
				ps.close();
				System.out.println("[CommandSync] New configuration file created.");
			}
			BufferedReader br = new BufferedReader(new FileReader(file + "/config.txt"));
			try{
				String l = br.readLine();
				Integer i = 0;
				while(l != null){
					data[i] = l.split("=")[1];
					i++;
					l = br.readLine();
				}
				System.out.println("[CommandSync] Configuration file loaded.");
			}finally{
				br.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return data;
	}
}
