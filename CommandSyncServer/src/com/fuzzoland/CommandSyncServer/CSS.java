package com.fuzzoland.CommandSyncServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.md_5.bungee.api.plugin.Plugin;

public class CSS extends Plugin{

	public ServerSocket server;
	public List<String> oq = Collections.synchronizedList(new ArrayList<String>());
	public String spacer;
	
	public void onEnable(){
		String[] data = loadConfig();
		try{
			server = new ServerSocket(Integer.parseInt(data[1]), 50, InetAddress.getByName(data[0]));
			System.out.println("[CommandSync] Opened server on " + data[0] + ":" + data[1] + ".");
			new ClientListener(this, Integer.parseInt(data[2])).start();
		}catch(Exception e){
			e.printStackTrace();
		}
		spacer = data[3];
	}
	
	private String[] loadConfig(){
		String[] data = new String[4];
		try{
			File file = getDataFolder();
			if(!file.exists()){
				file.mkdirs();
				OutputStream os = new FileOutputStream(file + "/config.txt");
				PrintStream ps = new PrintStream(os);
				ps.println("ip=localhost");
				ps.println("port=9190");
				ps.println("heartbeat=5000");
				ps.println("spacer=@#@");
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
