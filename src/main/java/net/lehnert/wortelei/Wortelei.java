package net.lehnert.wortelei;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Wortelei {
    private static final int MAX_VERSUCHE = 6;
    private static List<String> woerterAsList;
    private static Set<String> woerterAsSet;
    private String suchwort;
    private List<String> versuche;
    private String versuch;

    public Wortelei() {
        if (woerterAsList == null) {
            initialize();
        }
        int randomNum = ThreadLocalRandom.current().nextInt(0, woerterAsList.size());
        suchwort = woerterAsList.get(randomNum);
        versuche = new ArrayList<>();
    }

    private void initialize() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Wortelei.class.getClassLoader().getResourceAsStream("nomen.txt")));
        woerterAsList = reader.lines().collect(Collectors.toList());
        woerterAsSet = woerterAsList.stream().collect(Collectors.toSet());
    }

    public List<String> getVersuche() {
        return versuche;
    }

    public void setVersuche(List<String> versuche) {
        this.versuche = versuche;
    }

    public void setVersuch(String versuch) {
        this.versuch = versuch.toUpperCase();
    }

    public String getVersuch() {
        return versuch;
    }

    public String addVersuch() {
        String fehler = checkVersuch();
        if (fehler == null) {
            versuche.add(versuch);
        }
        return fehler;
    }

    private String checkVersuch() {
        if (versuche.contains(versuch)) {
            return "Das Wort '"+versuch+"' wurde bereits eingegeben";
        }
        if (!woerterAsSet.contains(versuch)) {
            return "Das Wort '"+versuch+"' ist nicht im Wörterbuch enthalten";
        }
        return null;
    }

    public boolean isGeloest() {
        return versuche.contains(suchwort);
    }

    public boolean keineVersucheMehrOffen() {
        return versuche.size()>=MAX_VERSUCHE;
    }

    public String getSuchwort() {
        return suchwort;
    }

    public String versucheAsTable() {
        StringBuffer buf = new StringBuffer();
        for(String v: versuche) {
            buf.append(prettyPrint(v));
        }
        return buf.toString();
    }

    private String prettyPrint(String versuch) {
        HashMap<String, Integer> haeufigkeiten = new HashMap<>();
        for(String buchstabe: suchwort.split("(?!^)")) {
            if (haeufigkeiten.containsKey(buchstabe)) {
                haeufigkeiten.put(buchstabe, haeufigkeiten.get(buchstabe)+1);
            } else {
                haeufigkeiten.put(buchstabe, 1);
            }
        }
        String[] output = new String[5];
        for(int i=0; i<versuch.length(); i++) {
            // zuerst die richtigen suchen
            String buchstabe = versuch.substring(i, i+1);
            String suchBuchstabe = suchwort.substring(i, i+1);
            if (buchstabe.equals(suchBuchstabe)) {
                output[i] = "<div class=\"tile\" data-state=\"correct\">"+buchstabe+"</div>";
                int anzahl = haeufigkeiten.get(buchstabe);
                if (anzahl > 1) {
                    haeufigkeiten.put(buchstabe, anzahl - 1);
                } else {
                    haeufigkeiten.remove(buchstabe);
                }
            }
        }

        for(int i=0; i<versuch.length(); i++) {
            if (output[i] != null) {
                continue; //war schon richtig
            }
            String buchstabe = versuch.substring(i, i+1);
            if (haeufigkeiten.containsKey(buchstabe)) {
                int anzahl = haeufigkeiten.get(buchstabe);
                output[i] = "<div class=\"tile\" data-state=\"present\">"+buchstabe+"</div>";//"<span style=\"background-color:skyblue;\">"+buchstabe+"</span>";
                if (anzahl == 1) {
                    haeufigkeiten.remove(buchstabe);
                } else {
                    haeufigkeiten.put(buchstabe, anzahl-1);
                }
            } else {
                output[i] = "<div class=\"tile\" data-state=\"absent\">"+buchstabe+"</div>";//"<span style=\"background-color:lightgrey;\">"+buchstabe+"</span>";
            }
        }
        return String.join(" ", output);
    }
}
