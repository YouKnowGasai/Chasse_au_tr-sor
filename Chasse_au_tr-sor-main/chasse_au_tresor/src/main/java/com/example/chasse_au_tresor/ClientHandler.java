package com.example.chasse_au_tresor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;

    List<Results> resultsList = new ArrayList<>();

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Message received from client: " + message);

                if (message.startsWith(Messages.CLICK.getMessage())) {
                    // Gérer le changement de tour
                    Serveur.switchPlayerTurn();
                    Serveur.broadcastMessage(message, this);
                } else if(message.startsWith(Messages.SCORE.getMessage())) {
                    resultsList.add(parseScore(message));
                    Serveur.broadcastMessage(message,this);
                } else if (message.startsWith(Messages.TCHAT.getMessage())) {
                    System.out.println("Handling chat message");
                    handleChatMessage(message, this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Results parseScore(String message) {
        String[] data = message.split(",");
        return new Results(
                Integer.parseInt(data[1]),
                Integer.parseInt(data[2]),
                Integer.parseInt(data[3])
        );
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public void handleChatMessage(String message, ClientHandler sender) {
        System.out.println("Handling chat message: " + message);
        Serveur.broadcastChatMessage(message, sender);
    }
}
