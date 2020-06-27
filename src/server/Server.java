package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Server {
	
	private static DatagramSocket socket;
	private static boolean running;
	
	// Holds all the connected clients info.
	private static ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>();
	
	private static int clientId = 0;
	
	// Starts the server
	public static void start(int port) {
		try {
			
			socket = new DatagramSocket(port);
			running = true;
			listen();
			
			System.out.println("Server started on port " + port);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Broadcasts message to all the clients
	private static void broadcast(String message) {
		
		for(ClientInfo client : clients) {
			send(message , client.getAddress(), client.getPort());
		}
		
	}
	
	// Sends message to specific client address
	private static void send(String message, InetAddress address, int port) {
		try {
			
			// Adds indicator for end of message string
			message += "\\e";
			byte[] data = message.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
			
			socket.send(packet);
			System.out.println("Sent message to " + address.getHostAddress() + ":" + port);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Listens for new incoming messages in a new thread
	private static void listen() {
		// Listener in a new thread waiting for incoming messages
		Thread listener = new Thread("Chat Engine Listener") {
			public void run() {
				try {
					while(running) {
						
						// Holds message in a data packet with host info.
						byte[] data = new byte[1024];
						DatagramPacket packet = new DatagramPacket(data, data.length);
						socket.receive(packet);
						
						// Changes message to string and removes end indicator
						String message = new String(data);
						message = message.substring(0, message.indexOf("\\e"));
						
						// If message is not server command -> broadcasts to all clients
						if(!isCommand(message, packet)) {
							broadcast(message);
						}
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}; listener.start();
	}
	
	/*
	 * All server commands-
	 * \\con:[name] - Connected user
	 * \\dis:[name] - Disconnected user
	 * \\cm: - connected message
	 * \\dm: - disconnected message
	 */
	
	// Checks if a message is a command of message
	private static boolean isCommand(String message , DatagramPacket packet) {
		if(message.startsWith("\\con:")) {
			// Connecting user command
			String name = message.substring(message.indexOf(":")+1);
			
			clients.add(new ClientInfo(name, clientId++, packet.getAddress(), packet.getPort()));
			broadcast("\\cm:User " + name + " connected!");
			
			return true;
		} else if(message.startsWith("\\dis:")) {
			// Disconnecting user command
			String name = message.substring(message.indexOf(":")+1);
			
			broadcast("\\dm:User " + name + " disconnected!");
			
			return true;
		}
		
		return false;
	}
	
	// Stops the server
	public static void stop() {
		running = false;
	}
	
}
