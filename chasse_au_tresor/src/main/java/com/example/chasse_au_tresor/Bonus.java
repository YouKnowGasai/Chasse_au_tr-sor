package com.example.chasse_au_tresor;

import java.util.LinkedList;
import java.util.List;

public class Bonus {

    String libelle;
    String description;
    String effet;
    List<Bonus> listeBonus = new LinkedList<>();


    public Bonus(){

    }

    public Bonus(String libelle, String description, String effet){
        this.libelle = libelle;
        this.description = description;
        this.effet = effet;
    }

    public void GenererListeBonus() {

        this.listeBonus.add(new Bonus("Provisions", "Vous avez trouvé des provisions !", "Augmente votre score"));
        this.listeBonus.add(new Bonus("Bourse", "Vous trouvez une bourse d'or", "Augmente votre score"));
        this.listeBonus.add(new Bonus("Indice", "Ceci est un indice concernant le trésor", "Augmente votre score"));
        this.listeBonus.add(new Bonus("Abri", "Vous avez trouvé un abri pour la nuit", "Augmente votre score"));
        this.listeBonus.add(new Bonus("Réparation", "Vous avez trouvé de quoi réparer le bateau", "Augmente votre score"));

    }

    public void SupprimerIndex(int randomNumber){

        listeBonus.remove(randomNumber);

    }

}
