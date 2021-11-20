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
    public static ArrayList<String> listanomi = new ArrayList<>();

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            this.nome = bufferedReader.readLine();

            clientHandlers.add(this);
            MessaggioBroadcast("SERVER: " + nome + " si è unito alla chat");

            listanomi.add(nome);
            MessaggioLista("utenti connessi : ");
            for (int i = 0; listanomi.size() > i; i++) {
                MessaggioLista(listanomi.get(i));
            }
            MessaggioLista("\n");

        } catch (IOException e) {

            chiudiTutto(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messaggioDalClient;

        while (socket.isConnected()) {
            try {

                messaggioDalClient = bufferedReader.readLine();
                if (messaggioDalClient.indexOf("@") != -1) {
                    comandi(messaggioDalClient);
                } else {
                    MessaggioBroadcast(messaggioDalClient);
                }
            } catch (IOException e) {
                chiudiTutto(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void MessaggioBroadcast(String messageToSend) {
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

    public void MessaggioLista(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                clientHandler.bufferedWriter.write(messageToSend);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();

            } catch (IOException e) {

                chiudiTutto(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void MessaggioSingolo(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.nome.equals(nome)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {

                chiudiTutto(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void MessaggioPrivato(String messageToSend, String nombre) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.nome.equals(nombre)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {

                chiudiTutto(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void rimuoviClientHandler() {

        clientHandlers.remove(this);
        listanomi.remove(posizioneNome(listanomi, this.nome));
        MessaggioBroadcast("SERVER: " + nome + " ha abbandonato la chat");
    }

    public void chiudiTutto(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

        rimuoviClientHandler();
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

    public void comandi(String messaggio){

        if(messaggio.equals(nome +": @exit")){
            rimuoviClientHandler(); 
        }else if(messaggio.equals(nome +": @lista")){
            MessaggioSingolo("Lista utenti : ");
            for(int i=0;listanomi.size()>i;i++){
                MessaggioSingolo(listanomi.get(i));
            } 
        }else if(messaggio.contains("@") && messaggio.contains("$")){
            String destinatarioMsg= messaggio.substring(messaggio.indexOf("@")+1, messaggio.indexOf("$"));
            String Msg= messaggio.substring(0, messaggio.indexOf("@")) + messaggio.substring(messaggio.indexOf("$")+1);
            
        
            if(verificaNome(listanomi, destinatarioMsg)){
                MessaggioPrivato(Msg, destinatarioMsg);
            }else{
                MessaggioSingolo("Non esiste questo utente");
            }

        }else{
            MessaggioSingolo("Non esiste questo comando");
        }      
    }

    
    public boolean verificaNome(ArrayList a, String name){
      
        for(int i=0; i<a.size(); i++){ if(name.equals(a.get(i))) return true; }
        
        return false;
      
    }

    public int posizioneNome(ArrayList a, String name){
      
        for(int i=0; i<a.size(); i++){ if(name.equals(a.get(i))) return i; }
        
        return -1;
      
    }
     /* 
     * public void MessaggiocontrolloNome(String nomedacontrollare){ for
     * (ClientHandler clientHandler : clientHandlers) {
     * 
     * try{ if ( verificaNome(listanomi, nomedacontrollare)==1) {
     * clientHandler.bufferedWriter.write("Nome inserito già presente");
     * clientHandler.bufferedWriter.newLine(); clientHandler.bufferedWriter.flush();
     * } } catch (IOException e) {
     * 
     * chiudiTutto(socket, bufferedReader, bufferedWriter); } } }
     */

}
