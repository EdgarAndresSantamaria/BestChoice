package ehu.das.bestchoice;

import android.content.Context;
import android.content.res.Resources;

class GeneralService {

    private static String user = null;
    private static boolean logged = false;

    /**
     * Devuelve el texto de la categoría en función de su id
     *
     * @param category id de la categoría
     * @param context  contexto de la actividad
     * @return texto de la categoría
     */
    static String getCategoryTag(int category, Context context) {
        Resources r = context.getResources();
        return r.getStringArray(R.array.categories)[category - 1];
    }

    /**
     * Devuelve el texto de la pregunta para la categoría
     *
     * @param category id de la categoría
     * @param context  contexto de la actividad
     * @return texto de la pregunta
     */
    static String getCategoryQuestion(int category, Context context) {
        Resources r = context.getResources();
        return r.getStringArray(R.array.category_questions)[category - 1];
    }

    /**
     * Activa el boolean de estar logeado
     */
    static void setLogged(String username) {
        user = username;
        logged = true;
    }


    /**
     * Desactiva el boolean de estar logeado
     */
    static void setUnlogged() {
        user = null;
        logged = false;
    }

    /**
     * Devuelve si el usuario está logeado
     *
     * @return logged
     */
    static boolean userLogged() {
        return logged;
    }

    /**
     * Devuelve el usuario activo
     *
     * @return username
     */
    static String getUsername() {
        return user;
    }
}
