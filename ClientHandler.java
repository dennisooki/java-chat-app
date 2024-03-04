
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.io.InputStreamReader;

public class ClientHandler implements Runnable{   //runnable so that each client instance is executed bya separate thread

    //keep track of all clients, static for class mutany ++loop 4 broadcast
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();    
    private Socket socket;   //socket passed from server class
    private BufferedReader bufferedReader;   //2 read messages sent from this client
    private BufferedWriter bufferedWriter;  //2 read messages sent from other clients
    private String clientUsername;

    //setting socket
    public ClientHandler(Socket socket){
        try{
            this.socket = socket;
            
            //out to send/write;                         wrapping characterstream on byte stream coz we wanna send over messages
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));   //++...Writer==characterStream  ...Stream==byteStream
            //bufferedwriter 2 increase efficiency/reduce r/w & chance of IOexceptions
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.clientUsername = bufferedReader.readLine();  //rmbr readline reads till newline
            clientHandlers.add(this); //this exact line of code is the best line of code i have ever written
                            //this reps da clientHandler object

            broadcastMessage("SERVER: " + clientUsername + " just entered da chat!");

        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter); //only closes current client so others are unaffected on catch 

        }
    }

    //eeverything under this override is what is actually run on a separate thread
    //why? bcoz waiting 4 a message is a blocking operation & if we werent using multiple threads for it then our application 
    //   would be stuck waiting fro mssgs & u wouldnt be able 2 send a mssg till u received one
    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()){
            try {
                messageFromClient=bufferedReader.readLine();  //remember this ref ?18
                broadcastMessage(messageFromClient);
            }catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break; //break out of while loop once client disconnects
            }
        }
    }
    public void broadcastMessage(String messageToSend){
        for (ClientHandler clientHandler : clientHandlers) {
            try{
                if (!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);   //here we send/broadcast da mssg to all other clients
                    clientHandler.bufferedWriter.newLine();   //remember ref ?18, client expects newline (this line just sends a newline character)
                    clientHandler.bufferedWriter.flush();   //coz buffer wont be flushed(sent) 2 ouput stream unless its full, essentially we're manually flushing 

                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                
            }
        }
    }
    //when user ameleft
    public void removeClientHandler(){
        broadcastMessage("SERVER: "+ clientUsername +" has left the chat!");
        clientHandlers.remove(this);
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            //below is coz null exceptions are very common here 
            //note: we do this in a separate method 2 reduce repition and also bcoz i hate nested try catch blocks
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            //with streams underlying streams r closed when u close the wrapper hence y we only close these 2 and not the outputstreamwriter etc
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            //closing socket also closes its i/o streams
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();    //if an error occurs here send me this stacktrace i was unable to recreate an error i got and i didnt have this stacktrace printed
        }
    }
}