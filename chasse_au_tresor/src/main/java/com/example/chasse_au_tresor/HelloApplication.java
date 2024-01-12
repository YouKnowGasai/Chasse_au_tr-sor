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

    Bonus listeBonus = new Bonus();

    Malus listeMalus = new Malus();

    int indexBonus = 5;

    int indexMalus = 9;


    @Override
    public void start(Stage primaryStage) {

        listeBonus.GenererListeBonus();
        listeMalus.GenererListeMalus();

        List<Malus> tableauEvent = tableauEvent();

        System.out.println(listeBonus.listeBonus.get(1).libelle);
        System.out.println(listeMalus.listeMalus.get(2).libelle);

        // Création d'un GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));

        // Ajout de cases (Label) au GridPane avec gestionnaire d'événements
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Label caseLabel = createCase("Case " + (row * 10 + col + 1));
                caseLabel.setUserData(tableauEvent.get(row * 10 + col).libelle);
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
        System.out.println("Case cliquée : " + (row * 10 + col + 1) + " " + caseLabel.getUserData());
        caseLabel.setText(caseLabel.getUserData().toString());


        // Changer le fond de la case
        caseLabel.setStyle("-fx-background-color: lightblue; -fx-border-color: black; -fx-padding: 5px;");
    }


    public List<Malus> tableauEvent(){

        List<Malus> listeEvent = new LinkedList<>();

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (indexMalus == -1 ){
                    listeEvent.add(listeMalus.listeMalus.get(10));
                }else {

                    listeEvent.add(listeMalus.listeMalus.get(indexMalus));
                    System.out.println();
                    indexMalus--;
                }
            }
        }

        Collections.shuffle(listeEvent);

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                System.out.println(listeEvent.get(row * 10 + col).libelle);
            }
        }

        return listeEvent;
    }


    public static void main(String[] args) {
        launch(args);
    }

}


