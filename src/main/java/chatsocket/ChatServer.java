package chatsocket;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Hello world!
 *
 */
public class ChatServer 
{

        private final ServerSocket serverSocket;
        
        public ChatServer(ServerSocket ServerSocket){

            this.serverSocket = ServerSocket;
        }
    

        public void startServer(){

            try{

                while(!serverSocket.isClosed()){
                    // nel client Handler, il server socket sarà chiuso
                    Socket socket = serverSocket.accept();
                    System.out.println("Il client si è unito alla chat");
                    ClientHandler clientHandler = new ClientHandler(socket);
                    Thread thread = new Thread(clientHandler);
                    thread.start();

                }
             } catch (IOException e){
                    closeServerSocket();
                }
            }

                public void closeServerSocket(){

                    try {
                        
                        if(serverSocket != null){

                            serverSocket.close();
                        }

                    } catch (IOException e) {
                        
                        e.printStackTrace();

                    }
                }

            }
        }


    }
    public static void main( String[] args )
    {

        ServerSocket serverSocket = new ServerSocket(1234);
        ChatServer server = new ChatServer(serverSocket);
        server.startServer();


    }



