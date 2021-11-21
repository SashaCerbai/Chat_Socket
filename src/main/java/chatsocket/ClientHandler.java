package chatsocket;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

//Problemi con exit

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String nome;
    public static ArrayList<String> listanomi = new ArrayList<>();
    public static int numDoppione = 1;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            clientHandlers.add(this);
            this.nome = bufferedReader.readLine();
            nome = MessaggiocontrolloNome(nome);

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
        System.out.println("Un utente è uscito dalla chat");
        chiudiTutto(socket, bufferedReader, bufferedWriter);

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

    public void comandi(String messaggio) {

        if (messaggio.equals(nome + ": @exit")) {
            rimuoviClientHandler();
        } else if (messaggio.equals(nome + ": @help")) {
            MessaggioSingolo(
                    "Comandi speciali: \n @exit : Comando pe uscire dalla chat \n @lista : Comando per vedere la lista degli utenti connessi \n @nome$messaggio : Comando per scrivere privatamente ad un utente");
        } else if (messaggio.equals(nome + ": @lista")) {
            MessaggioSingolo("Lista utenti : ");
            for (int i = 0; listanomi.size() > i; i++) {
                MessaggioSingolo(listanomi.get(i));
            }
        } else if (messaggio.contains("@") && messaggio.contains("$")) {
            String destinatarioMsg = messaggio.substring(messaggio.indexOf("@") + 1, messaggio.indexOf("$"));
            String Msg = messaggio.substring(0, messaggio.indexOf("@"))
                    + messaggio.substring(messaggio.indexOf("$") + 1);

            if (verificaNome(listanomi, destinatarioMsg)) {
                MessaggioPrivato(Msg, destinatarioMsg);
            } else {
                MessaggioSingolo("Non esiste questo utente");
            }

        } else {
            MessaggioSingolo("Non esiste questo comando");
        }
    }

    public boolean verificaNome(ArrayList a, String name) {

        for (int i = 0; i < a.size(); i++) {
            if (name.equals(a.get(i)))
                return true;
        }

        return false;

    }

    public int posizioneNome(ArrayList a, String name) {

        for (int i = 0; i < a.size(); i++) {
            if (name.equals(a.get(i)))
                return i;
        }

        return -1;

    }

    public String MessaggiocontrolloNome(String nomedacontrollare) {

        if (verificaNome(listanomi, nomedacontrollare)) {
            String valore = String.valueOf(numDoppione);
            int pos = posizioneNome(listanomi, nomedacontrollare);
            nome = listanomi.get(pos);
            nome += valore;
            numDoppione++;
            MessaggioSingolo("Il nome utente è stato modificato perché un'altro utente lo aveva già");
        } else {
            MessaggioSingolo("Il nome utente non riscontra problemi");
        }
        return nome;

    }

}
