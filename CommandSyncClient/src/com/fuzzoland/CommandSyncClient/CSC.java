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
	public String spacer = "@#@";
	public String user;
	public String pass;
	
	public void onEnable(){
		String[] data = loadConfig();
		if(data[3].equals("UNSET") || data[4].equals("UNSET") || data[5].equals("UNSET")){
			System.out.println("[CommandSync] !!! THE CONFIG FILE CONTAINS UNSET VALUES - YOU MUST FIX THEM BEFORE THE PLUGIN WILL WORK !!! ");
			return;
		}
		try{
			client = new ClientThread(this, InetAddress.getByName(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), data[3]);
			client.start();
		}catch(Exception e){
			e.printStackTrace();
		}
		user = data[4];
		pass = data[5];
		loadData();
		getCommand("Sync").setExecutor(new CommandSynchronize(this));
	}
	
	public void onDisable(){
		saveData();
	}
	
	private String[] loadConfig(){
		String[] defaults = new String[]{
			"ip=localhost", "port=9190", "heartbeat=1000", "name=UNSET", "user=UNSET", "pass=UNSET"
		};
		String[] data = new String[defaults.length];
		try{
			File file = new File(getDataFolder(), "config.txt");
			if(!file.exists()){
				file.createNewFile();
			}
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			BufferedReader br = new BufferedReader(new FileReader(file));
			for(int i = 0; i < defaults.length; i++){
				String l = br.readLine();
				if(l == null){
					ps.println(defaults[i]);
					data[i] = defaults[i].split("=")[1];
				}else{
					data[i] = l.split("=")[1];
				}
			}
			ps.close();
			br.close();
			System.out.println("[CommandSync] Configuration file loaded.");
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
				ps.println("oq:" + s);
			}
			ps.println("qc:" + String.valueOf(qc));
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
						if(l.startsWith("oq:")){
							oq.add(new String(l.substring(3)));
						}else if(l.startsWith("qc:")){
							qc = Integer.parseInt(new String(l.substring(3)));
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
