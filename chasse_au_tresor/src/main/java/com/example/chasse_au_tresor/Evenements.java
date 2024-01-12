package com.example.chasse_au_tresor;

import java.util.LinkedList;
import java.util.List;

public class Evenements {

    String libelle;
    String description;
    String effet;
    String type;
    List<Evenements> listeEvenements = new LinkedList<>();

    public Evenements(){

    }

    public Evenements(String libelle, String description, String effet, String type){
        this.libelle = libelle;
        this.description = description;
        this.effet = effet;
        this.type = type;
    }

    public void GenererListeEvenements() {

        this.listeEvenements.add(new Evenements("Pirate ", "Vous finissez blessé par des pirates", "Diminue votre score", "malus"));
        this.listeEvenements.add(new Evenements("Scorpion ", "Vous tombez sur un scorpion qui vous piques", "Diminue votre score", "malus"));
        this.listeEvenements.add(new Evenements("Piége", "Vous tombez dans un piége", "Diminue votre score", "malus"));
        this.listeEvenements.add(new Evenements("Tempête", "Une tempête s'abat sur l'île !", "Diminue votre score", "malus"));
        this.listeEvenements.add(new Evenements("Maladie", "Vous tombez malade à cause de l'eau que vous avez bu", "Diminue votre score", "malus"));
        this.listeEvenements.add(new Evenements("Provisions", "Vous avez trouvé des provisions !", "Augmente votre score", "bonus"));
        this.listeEvenements.add(new Evenements("Bourse", "Vous trouvez une bourse d'or", "Augmente votre score","bonus"));
        this.listeEvenements.add(new Evenements("Indice", "Ceci est un indice concernant le trésor", "Augmente votre score", "bonus"));
        this.listeEvenements.add(new Evenements("Abri", "Vous avez trouvé un abri pour la nuit", "Augmente votre score", "bonus"));
        this.listeEvenements.add(new Evenements("Réparation", "Vous avez trouvé de quoi réparer le bateau", "Augmente votre score","bonus"));
        this.listeEvenements.add(new Evenements("Victoire", "Vous avez trouvé trouvé le trésor", "Augmente votre score", "victoire"));
        this.listeEvenements.add(new Evenements("Défaite", "Vous avez succombé", "Augmente votre score","défaite"));
        this.listeEvenements.add(new Evenements("vide", "vide", "vide","vide"));

    }

}
