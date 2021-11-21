package chatsocket;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Hello World :)-|-(
public class ChatClient {
    
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    public String nome;

    public ChatClient(Socket socket, String nome) {
        try {
            this.socket = socket;
            this.nome=nome;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            chiudiTutto(socket, bufferedReader, bufferedWriter);
        }

    }

    public void mandaMessaggio() {
        try {
            bufferedWriter.write(nome);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            while (socket.isConnected()) {
                String messaggio = scanner.nextLine();
                bufferedWriter.write(nome + ": " + messaggio);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }

        } catch (IOException e) {
            chiudiTutto(socket, bufferedReader, bufferedWriter);
        }

    }

    public void ascoltoMessaggio() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgDallaChat;
                while (socket.isConnected()) {
                    try {
                        msgDallaChat = bufferedReader.readLine();
                        System.out.println(msgDallaChat);
                    } catch (IOException e) {
                        chiudiTutto(socket, bufferedReader, bufferedWriter);
                    }

                }
            }
        }).start();

    }

    public void chiudiTutto(Socket socket, BufferedReader bufferedreader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedreader != null) {
                bufferedreader.close();
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

    public static void main(String[] args) throws IOException {

        String nomeInserire;
        Scanner scanner = new Scanner(System.in);

        System.out.print("Inserisci il tuo nome per entrare nella chat: ");
        nomeInserire = scanner.nextLine();

        Socket socket = new Socket("localhost", 1234);

        ChatClient client = new ChatClient(socket, nomeInserire);

        client.ascoltoMessaggio();
        client.mandaMessaggio();
    }

}
