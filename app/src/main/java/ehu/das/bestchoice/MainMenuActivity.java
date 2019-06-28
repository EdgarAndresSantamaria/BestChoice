package ehu.das.bestchoice;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.PrintWriter;

import javax.net.ssl.HttpsURLConnection;

public class MainMenuActivity extends AppCompatActivity {
    private int selectedCategory = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean style = prefs.getBoolean("estilo", false);
        // check style settings
        if (style) {
            // if personalized
            setTheme(R.style.AppTheme1);
        } else {
            // if default
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_main_menu);

        loggedContent();
        setListeners();
    }

    /**
     * Cambia la interfaz en función de si el usuario ha inciado sesión
     */
    private void loggedContent() {
        if (GeneralService.userLogged()) {
            TextView welcomeBack = findViewById(R.id.welcome_message);
            String message = String.format(getString(R.string.mainmenu_welcome_logged), GeneralService.getUsername());
            welcomeBack.setText(message);
            welcomeBack.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Añade la funcionalidad a los botones
     */
    private void setListeners() {
        Button buttonPlay = findViewById(R.id.buttonMainMenuPlay);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        Button buttonAddDuel = findViewById(R.id.buttonMainMenuAddDuel);
        buttonAddDuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDuel();
            }
        });

        Button buttonStats = findViewById(R.id.buttonMainMenuStats);
        buttonStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stats();
            }
        });

        Button buttonRanking = findViewById(R.id.buttonMainMenuRanking);
        buttonRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ranking();
            }
        });

        Button buttonPreferences = findViewById(R.id.buttonPreferences);
        buttonPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences();
            }
        });

        if (GeneralService.userLogged()) {
            Button buttonLogin = findViewById(R.id.buttonLogin);
            buttonLogin.setText(R.string.close_session);
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeSession();
                }
            });
        }else {
            Button buttonLogin = findViewById(R.id.buttonLogin);
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login();
                }
            });
        }
    }

    /**
     * Pone en marcha la actividad de duelos
     */
    private void play() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.mainmenu_category_title));
        final CharSequence[] opciones = getResources().getStringArray(R.array.categories);
        builder.setSingleChoiceItems(opciones, selectedCategory - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectCategory(which);
            }
        });
        builder.setPositiveButton(getString(R.string.app_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playWithCategory();
                    }
                });
        builder.setNegativeButton(getString(R.string.app_no), null);
        builder.show();
    }

    /**
     * Guarda la categoría seleccionada del dialog
     *
     * @param category categoría
     */
    private void selectCategory(int category) {
        selectedCategory = category + 1;
    }

    /**
     * En función de la categoría seleccionada comienza la actividad
     */
    private void playWithCategory() {
        Intent i = new Intent(this, DuelActivity.class);
        i.putExtra("category", selectedCategory);
        startActivity(i);
    }

    /**
     * Pasa a la actividad de crear duelo
     */
    private void addDuel() {
        if (!GeneralService.userLogged()) {
            loginToast();
        } else {
            Intent i = new Intent(this, AddDuelActivity.class);
            startActivity(i);
        }
    }

    /**
     * Abre la actividad que muestra las estadísticas
     */
    private void stats() {
        if (!GeneralService.userLogged()) {
            loginToast();
        } else {
            Intent i = new Intent(this, StatsActivity.class);
            startActivity(i);
        }
    }

    /**
     * Abre la actividad del ranking de participación de usuarios
     */
    private void ranking() {
        if (!GeneralService.userLogged()) {
            loginToast();
        } else {
            Intent i = new Intent(this, RankingActivity.class);
            startActivity(i);
        }
    }

    /**
     * Abre la actividad de preferencias para seleccionar tema e idioma
     */
    private void preferences() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * This method allows user to login into bestChoice
     */
    private void login() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * Muestra un toast indicando al usuario que debe logearse para
     * utilizar ciertas funcionalidades
     */
    private void loginToast() {
        Toast.makeText(this, getString(R.string.mainmenu_error_login), Toast.LENGTH_LONG).show();
    }

    /**
     * This method closes current user session
     */
    private void closeSession(){
        String URL = "https://134.209.235.115/mabad008/WEB/gestorUsuarios.php";
        // get shared info
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor= prefs.edit();
        // reset terns y user shared info
        editor.putBoolean("terns", false);
        editor.putString("user","invitado");
        editor.commit();
        GeneralService.setUnlogged();
        // close remote session
        HttpsURLConnection urlConnection= ehu.das.bestchoice.GeneradorConexionesSeguras.getInstance().crearConexionSegura(getApplicationContext(),URL);
        try {
            // set request parameters
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            JSONObject parametrosJSON = new JSONObject();
            parametrosJSON.put("register", false);
            // write JSON parameters
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametrosJSON.toString());
            out.close();
            if (urlConnection.getResponseCode() == 200){
                // launch request
            }
            // catch any exception
        }catch(Exception e){

        }
        //launch main activity
        Intent i = new Intent(this , MainMenuActivity.class);
        startActivity(i);
        finish();
    }
}
