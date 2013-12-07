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
	public Integer qc = 0;
	public String spacer;
	
	public void onEnable(){
		String[] data = loadConfig();
		if(data[4].equals("UNSET")){
			System.out.println("[CommandSync] !!! YOU MUST SET THE SERVER'S NAME IN THE CONFIG BEFORE THE PLUGIN WILL WORK FOR THIS SERVER !!!");
			return;
		}
		try{
			client = new ClientThread(this, InetAddress.getByName(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), data[4]);
			client.start();
		}catch(Exception e){
			e.printStackTrace();
		}
		spacer = data[3];
		loadData();
		getCommand("Sync").setExecutor(new CommandSynchronize(this));
	}
	
	public void onDisable(){
		saveData();
	}
	
	private String[] loadConfig(){
		String[] data = new String[5];
		try{
			File file = getDataFolder();
			if(!file.exists()){
				file.mkdirs();
				OutputStream os = new FileOutputStream(file + "/config.txt");
				PrintStream ps = new PrintStream(os);
				ps.println("ip=localhost");
				ps.println("port=9190");
				ps.println("heartbeat=1000");
				ps.println("spacer=@#@");
				ps.println("name=UNSET");
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
	
	private void saveData(){
		try{
			OutputStream os = new FileOutputStream(new File(getDataFolder(), "data.txt"));
			PrintStream ps = new PrintStream(os);
			for(String s : oq){
				ps.println("q:" + s);
			}
			ps.println("c:" + String.valueOf(qc));
			ps.close();
			System.out.println("[CommandSync] All data saved.");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void loadData(){
		try{
			File file = new File(getDataFolder(), "data.txt");
			if(file.exists()){
				BufferedReader br = new BufferedReader(new FileReader(file));
				try{
					String l = br.readLine();
					while(l != null){
						if(l.startsWith("q:")){
							oq.add(new String(l.substring(2)));
						}else if(l.startsWith("c:")){
							qc = Integer.parseInt(new String(l.substring(2)));
						}
						l = br.readLine();
					}
					System.out.println("[CommandSync] All data loaded.");
				}finally{
					br.close();
				}
			}else{
				System.out.println("[CommandSync] A data file was not found. If this is your first start-up with the plugin, this is normal.");
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
