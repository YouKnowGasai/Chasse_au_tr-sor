package com.example.chasse_au_tresor;

import kotlin.random.URandomKt;

import java.util.LinkedList;
import java.util.List;

public class Malus {

    String libelle;
    String description;
    String effet;
    List<Malus> listeMalus = new LinkedList<>();

    public Malus(){

    }

    public Malus(String libelle, String description, String effet){
        this.libelle = libelle;
        this.description = description;
        this.effet = effet;
    }

    public void GenererListeMalus() {

        this.listeMalus.add(new Malus("Pirate ", "Vous finissez blessé par des pirates", "Diminue votre score"));
        this.listeMalus.add(new Malus("Scorpion ", "Vous tombez sur un scorpion qui vous piques", "Diminue votre score"));
        this.listeMalus.add(new Malus("Piége", "Vous tombez dans un piége", "Diminue votre score"));
        this.listeMalus.add(new Malus("Tempête", "Une tempête s'abat sur l'île !", "Diminue votre score"));
        this.listeMalus.add(new Malus("Maladie", "Vous tombez malade à cause de l'eau que vous avez bu", "Diminue votre score"));
        this.listeMalus.add(new Malus("Provisions", "Vous avez trouvé des provisions !", "Augmente votre score"));
        this.listeMalus.add(new Malus("Bourse", "Vous trouvez une bourse d'or", "Augmente votre score"));
        this.listeMalus.add(new Malus("Indice", "Ceci est un indice concernant le trésor", "Augmente votre score"));
        this.listeMalus.add(new Malus("Abri", "Vous avez trouvé un abri pour la nuit", "Augmente votre score"));
        this.listeMalus.add(new Malus("Réparation", "Vous avez trouvé de quoi réparer le bateau", "Augmente votre score"));
        this.listeMalus.add(new Malus("vide", "vide", "vide"));

    }

    public void SupprimerMalus(int randomNumber){

        listeMalus.remove(randomNumber);

    }

}
