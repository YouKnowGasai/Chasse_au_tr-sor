package com.example.chasse_au_tresor;

public enum Messages {
    PLAYERTURN("Tour du joueur "),
    SCORE("Resultat Joueur"),
    CLICK("Case cliquée"),
    TCHAT("Message");


    private final String message;
    Messages(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
