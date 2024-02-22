package com.example.chasse_au_tresor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class HelloApplication extends Application {

    public static final String DEFAITE = "défaite";
    public static final String VICTOIRE = "victoire";
    public static final int ROWS = 4;
    public static final int COLUMNS = 4;
    Evenements evenements = new Evenements();

    int indexEvenements = 11;

    private static Socket socket;
    private static PrintWriter writer;
    private static BufferedReader reader;

    private static int playerId;
    private static boolean isMyTurn = false;

    private static Label actionLabel = new Label("");

    private static ListView<String> chatListView;
    private static TextField chatTextField;
    private static Stage stage;

    private static int playerScore = 0;
    private static Label scoreLabel;

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

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        // Vérifiez l'ID du joueur passé en argument
        if (getParameters().getRaw().size() != 1) {
            System.out.println("Veuillez fournir l'ID du joueur (1 ou 2) en tant qu'argument de programme.");
            System.exit(1);
        }
        playerId = Integer.parseInt(getParameters().getRaw().get(0));
        scoreLabel = new Label("Score: " + playerScore);

        evenements.GenererListeEvenements();

        List<Evenements> tableauEvent = creerTableauEvent();

        // Création d'un GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));

        gridPane.setStyle("-fx-background-color: transparent;" +
                "-fx-background-image: url('Image/arton42-31192.png');" +
                "-fx-background-position: center center;" +
                "-fx-background-repeat: no-repeat;" +
                "-fx-background-size: cover;");

        // Ajout de cases (Label) au GridPane avec gestionnaire d'événements
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Label caseLabel = createCase("Case " + (row * ROWS + col + 1));
                caseLabel.setUserData(tableauEvent.get(row * ROWS + col).libelle + "@" + tableauEvent.get(row * ROWS + col).type);
                int finalRow = row;
                int finalCol = col;
                caseLabel.setOnMouseClicked(event -> handleCaseClick(caseLabel, finalRow, finalCol));
                gridPane.add(caseLabel, col, row);
            }
        }

        isMyTurn = (playerId == 1);

        if (isMyTurn) {
            actionLabel.setText("C'est votre tour (Joueur " + playerId + ")");
        } else {
            actionLabel.setText("En attente du tour du joueur...");
        }

        // Création des étiquettes pour chaque joueur
        Label playerLabel = new Label("Joueur " + playerId);

        // Création des composants du chat
        chatListView = new ListView<>();
        chatTextField = new TextField();
        Button sendButton = new Button("Envoyer");
        sendButton.setOnAction(event -> sendMessage(chatTextField.getText()));

        // Mise en page du chat
        VBox chatBox = new VBox(5, new Label("Chat"), chatListView, new HBox(chatTextField, sendButton));


        // Création de la mise en page finale
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(playerLabel, actionLabel, gridPane, scoreLabel, chatBox);

        // Création de la scène
        Scene scene = new Scene(layout);

        // Configuration de la scène
        primaryStage.setTitle("Chasse au trésor | joueur " + playerId);
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
            Platform.runLater(() -> {
                chatListView.getItems().add("La case est déjà cliqué");
            });
            writer.println(message);
            /*Traiter avec le serveur ce cas là*/
        } else {
            System.out.println("Case cliquée : " + (row * 10 + col + 1) + " " + caseLabel.getUserData().toString().split("@")[0]);
            boolean end = false;
            switch (caseLabel.getUserData().toString().split("@")[1]) {
                case "bonus":
                    caseLabel.setStyle("-fx-background-color: lightgreen; -fx-border-color: black; -fx-padding: 5px;");
                    playerScore += 500;
//                        System.out.println(caseLabel.getUserData().toString().split("@")[1]);
                    break;
                case "malus":
                    caseLabel.setStyle("-fx-background-color: #FF7F7F; -fx-border-color: black; -fx-padding: 5px;");
                    playerScore -= 500;
//                    if (Objects.equals(caseLabel.getUserData().toString().split("@")[1], "malus")){
                    break;
                case DEFAITE, VICTOIRE:
                    if (isEvenement(caseLabel, DEFAITE)) {
                        playerScore -= 1000;
                    }else if(isEvenement(caseLabel, VICTOIRE)){
                        playerScore += 1000;
                    }
                    end = true;
                    break;
                case "vide":
                    // Changer le fond de la case
                    caseLabel.setStyle("-fx-background-color: lightblue; -fx-border-color: black; -fx-padding: 5px;");
            }

            caseLabel.setText(caseLabel.getUserData().toString().split("@")[0]);
            StringBuilder message = new StringBuilder();
            if(!end){
                buildClickMessage(caseLabel, row, col, message);
            } else{
                if (caseLabel.getUserData().toString().split("@")[1] == DEFAITE) {
                    buildEndMessage(message, playerScore, playerId, 0);
                }else{
                    buildEndMessage(message, playerScore, playerId, 1);
                }
                writer.println(message);
                try{
                    socket.close();
                }catch (IOException ioException){
                    System.out.println("erreur fin de jeu");
                }
                System.exit(1);
            }
            System.out.println("Sending message to server: " + message);
            writer.println(message);
            caseLabel.setUserData("clique");
            isMyTurn = false;
            System.out.println("Passer le tour au joueur " + (isMyTurn ? 1 : 2));
            if (isMyTurn) {
                writer.println("Tour du joueur " + playerId);
            }
        }
    }

    private static void buildClickMessage(Label caseLabel, int row, int col, StringBuilder message) {
        message.append(Messages.CLICK.getMessage());
        message.append(" : ");
        message.append((row * 10 + col + 1));
        message.append(" ");
        message.append(caseLabel.getUserData().toString().split("@")[0]);
        message.append(" ");
        message.append("par");
        message.append(" ");
        message.append(playerId);
    }
    private static void buildEndMessage(StringBuilder message, Integer score, Integer playerId, Integer finJeu){
        message.append(Messages.SCORE.getMessage());
        message.append(",");
        message.append(score);
        message.append(",");
        message.append(playerId);
        message.append(",");
        message.append(finJeu);
    }

    boolean isEvenement(Label label, String evenement) {
        return Objects.equals(label.getUserData().toString().split("@")[1], evenement);
    }

    public List<Evenements> creerTableauEvent() {

        List<Evenements> tableauEvent = new LinkedList<>();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (indexEvenements == -1) {
                    tableauEvent.add(evenements.listeEvenements.get(12));
                } else {
                    tableauEvent.add(evenements.listeEvenements.get(indexEvenements));
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
            System.exit(1);
        }
    }


    private static void listenToServer() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Message reçu du serveur : " + message);
                if (message.startsWith("Case cliquée")) {
                    System.out.println("CASE");
                    handleCaseClickFromServer(message);
                } else if (message.startsWith("Tour du joueur")) {
                    System.out.println("TOUR");
                    handlePlayerTurn(Integer.parseInt(message.split(" ")[3]));
                } else if(message.startsWith(Messages.SCORE.getMessage())){
                    System.out.println("STOP");
                    handleScoreMessage(ClientHandler.parseScore(message));
                }else if (message.startsWith(Messages.TCHAT.getMessage())) {
                    System.out.println("TCHAT");
                    handleChatMessageFromServer(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EndOfGameException e){
            writer.println("Game ended");
            System.exit(1);
        }
    }

    private static void handlePlayerTurn(int playerTurn) {
        isMyTurn = (playerId == playerTurn);
        System.out.println("C'est le tour du joueur " + playerTurn);
        Platform.runLater(() -> {
            chatListView.getItems().add("C'est le tour du joueur " + playerTurn);
        });

        Platform.runLater(() -> {
            if (isMyTurn) {
                actionLabel.setText("C'est votre tour (Joueur " + playerTurn + ")");
                scoreLabel.setText("Score: " + playerScore);
            } else {
                actionLabel.setText("En attente du tour du joueur...");
                scoreLabel.setText("Score: " + playerScore);
            }
        });
    }

    private static void handleScoreMessage(Results results){
        String messageResult;
        if (results.finJeu() == 0) {
            messageResult = "Défaite : ";
        }else{
            messageResult = "Victoire : ";
        }
        // TODO : Conditionner le message sur l'alerte en fonction du résultat.
        try{
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Probleme victoire/défaite");
        }
        Platform.runLater(() -> {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Résultat du jeu");
            alert.setHeaderText(null);
            alert.setContentText(messageResult + " du joueur " + results.player() + " possède un score de : " + results.score());
            alert.showAndWait();

            if (results.player() == 2) {

                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Résultat du jeu");
                alert2.setHeaderText(null);
                if (results.finJeu() == 0 ) {
                    alert2.setContentText( "Victoire du joueur 1");
                }else {
                    alert2.setContentText(("Défaite du joueur 1"));
                }
                alert2.showAndWait();

                Platform.exit();
            }else {

                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle("Résultat du jeu");
                alert2.setHeaderText(null);
                if (results.finJeu() == 0 ) {
                    alert2.setContentText( "Victoire du joueur 2");
                }else {
                    alert2.setContentText(("Défaite du joueur 2"));
                }
                alert2.showAndWait();

                Platform.exit();
            }
                });
    }

    private static void handleChatMessageFromServer(String message) {
        // Logique pour traiter le message de chat du serveur et mettre à jour l'interface graphique
        System.out.println("Message du serveur : " + message);
        Platform.runLater(() -> {
            chatListView.getItems().add(message);
        });
    }



    private static void handleCaseClickFromServer(String message) {
        // Logique pour traiter le message du serveur et mettre à jour l'interface graphique
        System.out.println("Message du serveur : " + message);
        Platform.runLater(() -> {
            chatListView.getItems().add(message);
        });
    }


    private static void sendMessage(String message) {
        System.out.println("sendMessage called with message: " + message);

        writer.println("Message du joueur " + playerId + " : " + message);

    }


}


