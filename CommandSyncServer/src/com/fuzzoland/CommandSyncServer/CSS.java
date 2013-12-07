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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fuzzoland.CommandSyncServer.Metrics.Graph;

import net.md_5.bungee.api.plugin.Plugin;

public class CSS extends Plugin{

	public ServerSocket server;
	public List<String> oq = Collections.synchronizedList(new ArrayList<String>());
	public List<String> pq = Collections.synchronizedList(new ArrayList<String>());
	public Map<String, Integer> qc = new HashMap<String, Integer>();
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
		loadData();
		try{
		    Metrics metrics = new Metrics(this);
		    Graph graph1 = metrics.createGraph("Total queries sent");
		    graph1.addPlotter(new Metrics.Plotter(){
				public int getValue(){
					return oq.size();
				}
			});
		    Graph graph2 = metrics.createGraph("Total servers linked");
		    graph2.addPlotter(new Metrics.Plotter(){
				public int getValue(){
					return qc.keySet().size();
				}
			});
		    metrics.start();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void onDisable(){
		saveData();
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
				ps.println("heartbeat=1000");
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
	
	private void saveData(){
		try{
			OutputStream os = new FileOutputStream(new File(getDataFolder(), "data.txt"));
			PrintStream ps = new PrintStream(os);
			for(String s : oq){
				ps.println("q:" + s);
			}
			for(Entry<String, Integer> e : qc.entrySet()){
				ps.println("c:" + e.getKey() + spacer + String.valueOf(e.getValue()));
			}
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
							String[] parts = new String(l.substring(2)).split(spacer);
							qc.put(parts[0], Integer.parseInt(parts[1]));
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
