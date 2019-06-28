package ehu.das.bestchoice;

import android.util.Log;

class Duel {
    private String id;
    private String name1;
    private String name2;
    private int category;

    Duel(String id, String name1, String name2, int category) {
        this.id = id;
        this.name1 = name1;
        this.name2 = name2;
        this.category = category;
    }

    String getID() {
        return id;
    }

    String getFirstName() {
        return name1;
    }

    String getSecondName() {
        return name2;
    }

    int getCategory() {
        return category;
    }

    void log() {
        Log.d("duel", String.format("Duel with id %s: %s vs %s [%s]", id, name1, name2, category));
    }
}
