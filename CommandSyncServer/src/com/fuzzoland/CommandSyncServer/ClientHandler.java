package com.fuzzoland.CommandSyncServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread{

	private CSS plugin;
	private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Integer heartbeat = 0;
    private Integer qc = 0;
	
	public ClientHandler(CSS plugin, Socket socket, Integer heartbeat) throws IOException{
		this.plugin = plugin;
		this.socket = socket;
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.heartbeat = heartbeat;
		System.out.println("[CommandSync] Received new connection from " + socket.getInetAddress().getHostName() + ":" + socket.getPort() + ".");
	}

	public void run(){
		while(true){
			try{
				out.println("heartbeat");
				if(out.checkError()){
					System.out.println("[CommandSync] Connection from " + socket.getInetAddress().getHostName() + ":" + socket.getPort() + " has disconnected.");
					return;
				}
				if(in.ready()){
					String input = in.readLine();
					if(!input.equals("heartbeat")){
						System.out.println("[CommandSync] [" + socket.getInetAddress().getHostName() + ":" + socket.getPort() + "] " + "Received input - " + input);
						plugin.oq.add(input);
					}
				}
				Integer size = plugin.oq.size();
				if(size > qc){
					for(int i = qc; i < size; i++){
						String output = plugin.oq.get(i);
						out.println(output);
						System.out.println("[CommandSync] [" + socket.getInetAddress().getHostName() + ":" + socket.getPort() + "] " + "Sent output - " + output);
						qc++;
					}
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
