package com.fuzzoland.CommandSyncServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ClientHandler extends Thread{

	private CSS plugin;
	private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Integer heartbeat = 0;
    private String name;
	
	public ClientHandler(CSS plugin, Socket socket, Integer heartbeat) throws IOException{
		this.plugin = plugin;
		this.socket = socket;
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.heartbeat = heartbeat;
		this.name = in.readLine();
		if(!plugin.qc.containsKey(name)){
			plugin.qc.put(name, 0);
		}
		System.out.println("[CommandSync] Received new connection from " + socket.getInetAddress().getHostName() + ":" + socket.getPort() + " under name " + name + ".");
	}

	public void run(){
		while(true){
			try{
				out.println("heartbeat");
				if(out.checkError()){
					System.out.println("[CommandSync] Connection from " + socket.getInetAddress().getHostName() + ":" + socket.getPort() + " under name " + name + " has disconnected.");
					return;
				}
				while(in.ready()){
					String input = in.readLine();
					if(!input.equals("heartbeat")){
						System.out.println("[CommandSync] [" + socket.getInetAddress().getHostName() + ":" + socket.getPort() + "] [" + name + "] Received input - " + input);
						String[] data = input.split(plugin.spacer);
						if(data[0].equals("player")){
							String command = "/" + data[2].replaceAll("\\+", " ");
							if(data[1].equals("single")){
								String name = data[3];
								ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);
								if(player != null){
									player.chat(command);
								}
								System.out.println("[CommandSync] Ran command " + command + " for player " + name + ".");
							}else if(data[1].equals("all")){
								for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
									player.chat(command);
								}
								System.out.println("[CommandSync] Ran command " + command + " for all online players.");
							}
						}else{
							if(data[1].equals("bungee")){
								System.out.println("[CommandSync] A request to execute a BungeeCord command was received. Unfortunately this feature is not possible yet.");
							}else{
								plugin.oq.add(input);
							}
						}
					}
				}
				Integer size = plugin.oq.size();
				Integer count = plugin.qc.get(name);
				if(size > count){
					for(int i = count; i < size; i++){
						count++;
						String output = plugin.oq.get(i);
						String[] data = output.split(plugin.spacer);
						if(data[1].equals("single")){
							if(data[3].equals(name)){
								out.println(output);
								System.out.println("[CommandSync] [" + socket.getInetAddress().getHostName() + ":" + socket.getPort() + "] [" + name + "] Sent output - " + output);
							}
						}else{
							out.println(output);
							System.out.println("[CommandSync] [" + socket.getInetAddress().getHostName() + ":" + socket.getPort() + "] [" + name + "] Sent output - " + output);
						}
					}
					plugin.qc.put(name, count);
				}
				sleep(heartbeat);
			}catch(IOException e){
				e.printStackTrace();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
}
