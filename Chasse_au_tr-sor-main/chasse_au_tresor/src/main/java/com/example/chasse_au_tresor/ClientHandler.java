package com.example.chasse_au_tresor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;

    private static int currentPlayer = 1;

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

                if (message.startsWith("Case cliquée")) {
                    // Gérer le changement de tour
                    Serveur.switchPlayerTurn();
                    Serveur.broadcastMessage(message, this);
                } else {
                    // Gérer les autres types de messages
                    Serveur.broadcastMessage(message, this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void switchPlayerTurn() {
        // Changer le joueur actuel
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        System.out.println("Tour du joueur " + currentPlayer);
        // Informer tous les clients du changement de tour
        Serveur.broadcastMessage("Tour du joueur " + currentPlayer, null);
    }

    private void handleCaseClick(String message) {
        // Gérer les actions spécifiques liées au clic de case ici

        // Informer tous les clients du changement de tour
        switchPlayerTurn();
        Serveur.broadcastMessage("PlayerTurn " + Serveur.getCurrentPlayerTurn(), null);
    }

    public void sendMessage(String message) {
        writer.println(message);
    }
}
