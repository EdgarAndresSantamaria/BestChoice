package ehu.das.bestchoice;

import java.util.ArrayList;
import java.util.List;

class DuelList {

    private List<Duel> list;
    private int currentDuel;
    private String jsonArrayString;

    DuelList() {
        list = new ArrayList<>();
        currentDuel = 0;
    }

    /**
     * Añade un nuevo duelo en la última posición
     *
     * @param newDuel duelo a añadir
     */
    void addDuel(Duel newDuel) {
        list.add(newDuel);
    }

    /**
     * Devuelve el duelo actual
     *
     * @return duelo
     */
    Duel getCurrenDuel() {
        return list.get(currentDuel);
    }

    /**
     * Devuelve el índice del duelo actual
     *
     * @return duelo
     */
    int getCurrenDuelIndex() {
        return currentDuel;
    }

    /**
     * Avanza el índice del duelo actual en caso de que haya más
     * y responde indicándolo
     *
     * @return indica si hay más duelos en la lista
     */
    boolean nextDuel() {
        boolean moreDuels = true;
        if (currentDuel + 1 >= list.size()) {
            moreDuels = false;
        } else {
            currentDuel++;
        }
        return moreDuels;
    }

    /**
     * Muestra por consola los duelos de la lista
     */
    void logDuels() {
        for (Duel duel : list) {
            duel.log();
        }
    }

    /**
     * Selecciona el índice del duelo
     * @param i
     */
    void setCurrentDuelIndex(int i){
        currentDuel = i;
    }

    /**
     * Devuelve el String del JSON con los duelos
     *
     * @return
     */
    String getJsonArrayString(){
        return jsonArrayString;
    }

    /**
     * Guarda el String del JSON con los duelos
     * @param pJsonArrayString
     */
    void setJsonArrayString(String pJsonArrayString){
        jsonArrayString = pJsonArrayString;
    }
}
