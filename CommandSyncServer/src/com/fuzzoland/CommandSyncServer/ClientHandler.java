package com.fuzzoland.CommandSyncServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		this.heartbeat = heartbeat;
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		System.out.println("[CommandSync] Received new connection from " + socket.getInetAddress().getHostName() + ":" + socket.getPort() + ".");
		name = in.readLine();
		if(plugin.c.contains(name)){
		    System.out.println("[" + socket.getInetAddress().getHostName() + ":" + socket.getPort() + "] [" + name + "] Provided a name that is already connected.");
		    out.println("n");
		    socket.close();
		    return;
		}
		out.println("y");
		String pass = in.readLine();
		if(!pass.equals(plugin.pass)){
			System.out.println("[" + socket.getInetAddress().getHostName() + ":" + socket.getPort() + "] [" + name + "] Provided an invalid password.");
			out.println("n");
			socket.close();
			return;
		}
		out.println("y");
		if(!plugin.qc.containsKey(name)){
			plugin.qc.put(name, 0);
		}
		plugin.c.add(name);
		System.out.println("[CommandSync] Connection from " + socket.getInetAddress().getHostName() + ":" + socket.getPort() + " under name " + name + " has been authorised.");
	}

	public void run(){
		while(true){
			try{
				out.println("heartbeat");
				if(out.checkError()){
					System.out.println("[CommandSync] Connection from " + socket.getInetAddress().getHostName() + ":" + socket.getPort() + " under name " + name + " has disconnected.");
					plugin.c.remove(name);
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
								Boolean found = false;
								for(ProxiedPlayer player : plugin.getProxy().getPlayers()){
									if(name.equals(player.getName())){
										player.chat(command);
										System.out.println("[CommandSync] Ran command " + command + " for player " + name + ".");
										found = true;
										break;
									}
								}
								if(!found){
									if(plugin.pq.containsKey(name)){
										List<String> commands = plugin.pq.get(name);
										commands.add(command);
										plugin.pq.put(name, commands);
									}else{
										plugin.pq.put(name, new ArrayList<String>(Arrays.asList(command)));
									}
									System.out.println("[CommandSync] Since " + name + " is offline the command " + command + " will run when they come online.");
								}
							}else if(data[1].equals("all")){
								for(ProxiedPlayer player : plugin.getProxy().getPlayers()){
									player.chat(command);
								}
								System.out.println("[CommandSync] Ran command " + command + " for all online players.");
							}
						}else{
							if(data[1].equals("bungee")){
								String command = data[2].replaceAll("\\+", " ");
								plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), command);
								System.out.println("[CommandSync] Ran command /" + command + ".");
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
			}catch(Exception e){
				plugin.c.remove(name);
				e.printStackTrace();
			}
		}
	}
}
