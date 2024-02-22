package com.example.chasse_au_tresor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Serveur {

    private static final int PORT = 6789;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static int currentPlayerTurn = 1;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
                clients.add(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public static void broadcastChatMessage(String message, ClientHandler sender) {
        System.out.println("Broadcasting chat message: " + message);
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public static void switchPlayerTurn() {
        currentPlayerTurn = (currentPlayerTurn == 1) ? 2 : 1;
        broadcastMessage(Messages.PLAYERTURN.getMessage() + currentPlayerTurn, null);
    }

}
