
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server {
	/*ServerSocket holds server-side 
	 * endpoint that accepts incoming connections 
	 * from client applications*/

    private ServerSocket serverSocket ;
	 
    public Server(ServerSocket serverSocket) {
			this.serverSocket = serverSocket;
	}
	
	public void startServer() {
		//this keeps the server running indefinitely till we close it manually
		
		try {
			while (!serverSocket.isClosed()) {
				//waiting 4 a client 2 connect indefinitely
				//note that .accept() below is a blocking method(program halts here hadi mtu aconnect
				
				Socket socket = serverSocket.accept();
				//accept() method abv returns a Socket object representing the connection, we assign 2 da socket varible
				
				System.out.println("A new client has connected");
				
				//implimented later,2 handle each client on separate thread
				ClientHandler clientHandler = new ClientHandler(socket);
				
				//bcoz we cant handle every client on same thread  ++low-lvl
				Thread thread = new Thread(clientHandler);
				thread.start();
			}
			//this expection is common				
		} catch (IOException e) {

		}
	}
    public void closeServerSocket() {
    	try {
    		if (serverSocket != null) {
    			serverSocket.close();
    		}
    	} catch (IOException e){
    		e.printStackTrace();
    	}
    }
    public static void main(String[] args) throws IOException {

    	ServerSocket serverSocket = new ServerSocket(1738);
    	Server server = new Server(serverSocket);
    	server.startServer();
    }
}
