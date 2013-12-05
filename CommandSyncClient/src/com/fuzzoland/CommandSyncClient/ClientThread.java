package com.fuzzoland.CommandSyncClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientThread extends Thread{

	private CSC plugin;
	private InetAddress ip;
	private Integer port;
	private Boolean connected = false;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private Integer heartbeat = 0;
	private Integer qc = 0;
	
	public ClientThread(CSC plugin, InetAddress ip, Integer port, Integer heartbeat){
		this.plugin = plugin;
		this.ip = ip;
		this.port = port;
		this.heartbeat = heartbeat;
		connect();
	}
	
	public void run(){
		while(true){
			if(connected){
				out.println("heartbeat");
				if(out.checkError()){
					connected = false;
					System.out.println("[CommandSync] Lost connection to the server.");
				}else{
					try{
						Integer size = plugin.oq.size();
						if(size > qc){
							for(int i = qc; i < size; i++){
								String output = plugin.oq.get(i);
								out.println(output);
								System.out.println("[CommandSync] [" + socket.getInetAddress().getHostName() + ":" + socket.getPort() + "] " + "Sent output - " + output);
								qc++;
							}
						}
						if(in.ready()){
							String input = in.readLine();
							if(!input.equals("heartbeat")){
								System.out.println("[CommandSync] [" + socket.getInetAddress().getHostName() + ":" + socket.getPort() + "] " + "Received input - " + input);
								// Process input
							}
						}
					}catch(IOException e){
						e.printStackTrace();
					}					
				}
			}else{
				connect();
			}
			try{
				sleep(heartbeat);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	
	private void connect(){
		try{
			this.socket = new Socket(ip, port);
			this.out = new PrintWriter(socket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.connected = true;
			System.out.println("[CommandSync] Connected to " + ip.getHostName() + ":" + String.valueOf(port) + ".");
		}catch(IOException e){
			System.out.println("[CommandSync] Could not connect to the server.");
		}
	}
}
