package com.fuzzoland.CommandSyncClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.bukkit.Bukkit;

public class ClientThread extends Thread {

	private CSC plugin;
	private InetAddress ip;
	private Integer port;
	private Boolean connected = false;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private Integer heartbeat = 0;
	private String name;
	private String pass;
	private String version = "2.3";
	
	public ClientThread(CSC plugin, InetAddress ip, Integer port, Integer heartbeat, String name, String pass) {
		this.plugin = plugin;
		this.ip = ip;
		this.port = port;
		this.heartbeat = heartbeat;
		this.name = name;
		this.pass = pass;
		connect(false);
	}
	
	public void run() {
		while(true) {
			if(connected) {
				out.println("heartbeat");
				if(out.checkError()) {
					connected = false;
					plugin.debugger.debug("Lost connection to the server.");
				} else {
					try {
						Integer size = plugin.oq.size();
						Integer count = plugin.qc;
						if(size > count) {
							for(int i = count; i < size; i++) {
								count++;
								String output = plugin.oq.get(i);
								out.println(output);
								plugin.debugger.debug("[" + socket.getInetAddress().getHostName() + ":" + socket.getPort() + "] " + "Sent output - " + output);
							}
							plugin.qc = count;
						}
						while(in.ready()) {
							String input = in.readLine();
							if(!input.equals("heartbeat")) {
							    plugin.debugger.debug("[" + socket.getInetAddress().getHostName() + ":" + socket.getPort() + "] " + "Received input - " + input);
								String[] data = input.split(plugin.spacer);
								if(data[0].equals("console")) {
									String command = data[2].replaceAll("\\+", " ");
									Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
									plugin.debugger.debug("Ran command /" + command + ".");
								}
							}
						}
					} catch(IOException e) {
						e.printStackTrace();
					}					
				}
			} else {
				connect(true);
			}
			try {
				sleep(heartbeat);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void connect(Boolean sleep) {
		if(sleep) {
			try {
				sleep(10000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			socket = new Socket(ip, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(name);
			if(in.readLine().equals("n")) {
			    plugin.debugger.debug("The name " + name + " is already connected.");
			    socket.close();
			    return;
			}
			out.println(pass);
			if(in.readLine().equals("n")) {
			    plugin.debugger.debug("The password you provided is invalid.");
			    socket.close();
				return;
			}
            out.println(version);
            if(in.readLine().equals("n")) {
                plugin.debugger.debug("The client's version of " + version + " does not match the server's version of " + in.readLine() + ".");
                socket.close();
                return;
            }
			connected = true;
			plugin.debugger.debug("Connected to " + ip.getHostName() + ":" + String.valueOf(port) + " under name " + name + ".");
		} catch(IOException e) {
		    plugin.debugger.debug("Could not connect to the server.");
		}
	}
}
