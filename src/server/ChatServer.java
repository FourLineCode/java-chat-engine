package server;

public class ChatServer {
	
	// Server port
	private static final int PORT = 4565;

	public static void main(String[] args) {
		
		Server.start(PORT);

	}

}
