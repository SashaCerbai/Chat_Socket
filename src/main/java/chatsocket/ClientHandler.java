package chatsocket;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String nome;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            this.nome = bufferedReader.readLine();
            
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + nome + " si è unito alla chat");
        } catch (IOException e) {
            
            chiudiTutto(socket, bufferedReader, bufferedWriter);
        }
    }

    
    @Override
    public void run() {
        String messagioDalClient;
        
        while (socket.isConnected()) {
            try {
                
                messagioDalClient = bufferedReader.readLine();
                broadcastMessage(messagioDalClient);
            } catch (IOException e) {
                chiudiTutto(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
    
                if (!clientHandler.nome.equals(nome)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
    
                chiudiTutto(socket, bufferedReader, bufferedWriter);
            }
        }
    }

        public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + nome + " ha abbandonato la chat");
    }

    public void chiudiTutto(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
    
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


