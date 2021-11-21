package chatsocket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    private final ServerSocket serverSocket;

    public ChatServer(ServerSocket ServerSocket) {

        this.serverSocket = ServerSocket;
    }

    public void avviaServer() {

        try {

            while (!serverSocket.isClosed()) {
                // nel client Handler, il server socket sarà chiuso
                Socket socket = serverSocket.accept();
                System.out.println("Un utente si è unito alla chat");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();

            }

        } catch (IOException e) {

            chiudiServerSocket();
        }
    }

    public void chiudiServerSocket() {

        try {

            if (serverSocket != null) {

                serverSocket.close();
            }

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(1234);
        ChatServer server = new ChatServer(serverSocket);
        server.avviaServer();

    }

}
