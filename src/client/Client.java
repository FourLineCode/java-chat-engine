package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
	
	private DatagramSocket socket;
	private InetAddress address;
	private int port;
	private String name;
	
	private boolean running;
	
	// Constructor -> Creates client user
	public Client(String name, String address, int port) {
		try {
			
			this.address = InetAddress.getByName(address);
			this.port = port;
			this.name = name;
			
			socket = new DatagramSocket();
			running = true;
			listen();
			
			// Sends user connected command to server
			send("\\con:" + name);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Sends message/command to the server
	public void send(String message) {
		try {
			
			// If message is not command , add the client name
			if(!message.startsWith("\\")) {
				message = name + ": " + message;
			}
			
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
	
	// Listens for incoming messages for server
	private void listen() {
		Thread listener = new Thread("Chat Engine Listener") {
			public void run() {
				try {
					while(running) {
						
						byte[] data = new byte[1024];
						DatagramPacket packet = new DatagramPacket(data, data.length);
						socket.receive(packet);
						
						String message = new String(data);
						message = message.substring(0, message.indexOf("\\e"));
						
						// If message is not server command -> shows message on the window
						if(!isCommand(message)) {
							ClientWindow.printToConsole(message, 0);
						}
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}; listener.start();
	}
	
	// Checks if message is a server command
	private static boolean isCommand(String message) {
		if(message.startsWith("\\cm:")) {
			// User connected command message
			String connectedMessage = message.substring(message.indexOf(":")+1);
			ClientWindow.printToConsole(connectedMessage, 1);
			
			return true;
		} else if(message.startsWith("\\dm:")) {
			// User disconnected command message
			String disconnectedMessage = message.substring(message.indexOf(":")+1);
			ClientWindow.printToConsole(disconnectedMessage, 2);
			
			return true;
		}
		
		return false;
	}
	
}
