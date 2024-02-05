package com.example.chasse_au_tresor;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Executable;
import java.net.Socket;
import java.util.*;

public class HelloApplication extends Application {

    Evenements evenements = new Evenements();

    int indexEvenements = 11;

    private static Socket socket;
    private static PrintWriter writer;
    private static BufferedReader reader;

    private static int playerId;
    private static boolean isMyTurn = false;


    @Override
    public void start(Stage primaryStage) {

        // Vérifiez l'ID du joueur passé en argument
        if (getParameters().getRaw().size() != 1) {
            System.out.println("Veuillez fournir l'ID du joueur (1 ou 2) en tant qu'argument de programme.");
            System.exit(1);
        }
        playerId = Integer.parseInt(getParameters().getRaw().get(0));

        evenements.GenererListeEvenements();

        List<Evenements> tableauEvent = CreerTableauEvent();

        // Création d'un GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));

        // Ajout de cases (Label) au GridPane avec gestionnaire d'événements
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Label caseLabel = createCase("Case " + (row * 10 + col + 1));
                caseLabel.setUserData(tableauEvent.get(row * 10 + col).libelle + "@" + tableauEvent.get(row * 10 + col).type);
                int finalRow = row;
                int finalCol = col;
                caseLabel.setOnMouseClicked(event -> handleCaseClick(caseLabel, finalRow, finalCol));
                gridPane.add(caseLabel, col, row);
            }
        }

        isMyTurn = (playerId == 1);

        // Création des étiquettes pour chaque joueur
        Label playerLabel = new Label("Joueur " + playerId);
        Label actionLabel = new Label("Action du joueur : ");

        // Création de la mise en page finale
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(playerLabel, actionLabel, gridPane);

        // Création de la scène
        Scene scene = new Scene(layout);

        // Configuration de la scène
        primaryStage.setTitle("Tableau 10x10");
        primaryStage.setScene(scene);

        // Affichage de la fenêtre
        primaryStage.show();
    }

    private Label createCase(String text) {

        Label caseLabel = new Label(text);
        caseLabel.setMinSize(65, 65); // Définir la taille minimale de chaque case
        caseLabel.setStyle("-fx-border-color: black; -fx-padding: 5px;");

        return caseLabel;
    }

    private void handleCaseClick(Label caseLabel, int row, int col) {

        if (!isMyTurn) {
            System.out.println("Ce n'est pas encore votre tour.");
            return;
        }

        if (caseLabel.getUserData() == "clique") {
            System.out.println("la case est déjà cliqué" + playerId);
            String message = "la case est déjà cliqué";
            writer.println(message);
            /*Traiter avec le serveur ce cas là*/

        }else {
            System.out.println("Case cliquée : " + (row * 10 + col + 1) + " " + caseLabel.getUserData().toString().split("@")[0]);
            switch (caseLabel.getUserData().toString().split("@")[1]) {
                case "bonus", "victoire":

                    caseLabel.setStyle("-fx-background-color: lightgreen; -fx-border-color: black; -fx-padding: 5px;");

                    break;

                case "malus", "défaite":

                    caseLabel.setStyle("-fx-background-color: #FF7F7F; -fx-border-color: black; -fx-padding: 5px;");

                    break;

                case "vide":

                    // Changer le fond de la case
                    caseLabel.setStyle("-fx-background-color: lightblue; -fx-border-color: black; -fx-padding: 5px;");

            }

            caseLabel.setText(caseLabel.getUserData().toString().split("@")[0]);
            String message = "Case cliquée : " + (row * 10 + col + 1) + " " + caseLabel.getUserData().toString().split("@")[0] + " par " + playerId;
            System.out.println("Sending message to server: " + message);
            writer.println(message);

            caseLabel.setUserData("clique");

            isMyTurn = false;  // Assurez-vous que le tour du joueur est terminé après l'action

            System.out.println("Passer le tour au joueur " + (isMyTurn ? 1 : 2));

            if (isMyTurn) {
                writer.println("Tour du joueur " + playerId);
            }


        }
    }


    public List<Evenements> CreerTableauEvent(){

        List<Evenements> tableauEvent = new LinkedList<>();

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (indexEvenements == -1 ){
                    tableauEvent.add(evenements.listeEvenements.get(12));
                }else {

                    tableauEvent.add(evenements.listeEvenements.get(indexEvenements));
                    System.out.println();
                    indexEvenements--;
                }
            }
        }

        Collections.shuffle(tableauEvent);

        return tableauEvent;
    }

    private static void connectToServer() {
        try {
            // Remplacez "localhost" et 12345 par l'adresse IP et le port du serveur
            socket = new Socket("127.0.0.1", 6789);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Créer un thread pour gérer la réception des messages du serveur
            Thread serverListener = new Thread(HelloApplication::listenToServer);
            serverListener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void listenToServer() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Message reçu du serveur : " + message);
                if (message.startsWith("Case cliquée")) {
                    handleCaseClickFromServer(message);
                } else if (message.startsWith("Tour du joueur")) {
                    handlePlayerTurn(Integer.parseInt(message.split(" ")[3]));
                } else {
                    // Gérer d'autres types de messages du serveur si nécessaire
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handlePlayerTurn(int playerTurn) {
        isMyTurn = (playerId == playerTurn);
        System.out.println("C'est le tour du joueur " + playerTurn);
    }

    private static void handleCaseClickFromServer(String message) {
        // Logique pour traiter le message du serveur et mettre à jour l'interface graphique
        System.out.println("Message du serveur : " + message);
        mettreAJourInterfaceGraphique(message);
    }

    public static void mettreAJourInterfaceGraphique(String message) {
        // Ajoutez ici la logique pour mettre à jour l'interface graphique en fonction du message
        // ...
    }


    public static void main(String[] args) {
        if (args.length > 0) {
            playerId = Integer.parseInt(args[0]);
        } else {
            System.out.println("Veuillez fournir l'ID du joueur (1 ou 2) en tant qu'argument de programme.");
            System.exit(1);
        }

        connectToServer();
        launch(args);
    }

    private static void handleInitialInfo(String message) {
        // Traiter les informations initiales reçues du serveur (par exemple, initialiser le plateau de jeu).
        System.out.println("Informations initiales reçues : " + message);
    }

}


