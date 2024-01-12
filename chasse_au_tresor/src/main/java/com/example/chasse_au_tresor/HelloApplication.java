package com.example.chasse_au_tresor;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Executable;
import java.util.*;

public class HelloApplication extends Application {

    Evenements evenements = new Evenements();

    int indexEvenements = 11;


    @Override
    public void start(Stage primaryStage) {

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

        // Création de la scène
        Scene scene = new Scene(gridPane);

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
        System.out.println("Case cliquée : " + (row * 10 + col + 1) + " " + caseLabel.getUserData().toString().split("@")[0]);
        switch (caseLabel.getUserData().toString().split("@")[1]) {
            case "bonus", "victoire":

                caseLabel.setStyle("-fx-background-color: lightgreen; -fx-border-color: black; -fx-padding: 5px;");

                break;

            case "malus", "défaite":

                caseLabel.setStyle("-fx-background-color: #FF7F7F; -fx-border-color: black; -fx-padding: 5px;");

                break;

            case "vide" :

                // Changer le fond de la case
                caseLabel.setStyle("-fx-background-color: lightblue; -fx-border-color: black; -fx-padding: 5px;");

        }

        caseLabel.setText(caseLabel.getUserData().toString().split("@")[0]);
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


    public static void main(String[] args) {
        launch(args);
    }

}


