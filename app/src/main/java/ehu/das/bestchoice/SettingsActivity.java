package ehu.das.bestchoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private Switch color;
    private Switch language;

    //TODO: change personalized colour palette THeme2 (consistency with App)

    /**
     * This method creates the GUI asssociated to setting sctivity
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean estilo = prefs.getBoolean("estilo", false);
        // check style settings
        if (estilo) {
            // if personalized
            setTheme(R.style.AppTheme1);
        } else {
            // if default
            setTheme(R.style.AppTheme);
        }

        setContentView(R.layout.activity_settings);


        boolean style = prefs.getBoolean("estilo", false);
        boolean lang = prefs.getBoolean("ingles", false);

        // retrieve the GUI components to manage preferences
        color = (Switch) findViewById(R.id.switch1);
        language = (Switch) findViewById(R.id.switch2);

        if (lang) {
            language.setChecked(true);
        }

        if (style) {
            color.setChecked(true);
        }

        FloatingActionButton fab7 = (FloatingActionButton) findViewById(R.id.fab7);
        fab7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMain();
            }
        });
    }

    /**
     * Manages the back button logic
     */
    @Override
    public void onBackPressed() {
        startMain();
        finish();
    }

    /**
     * This method starts the activity Main after checking preferences status
     */
    public void startMain() {
        // load new color prefs
        if (color.isChecked()) {
            setTheme(true);
        } else {
            setTheme(false);
        }

        // load new language prefs
        if (language.isChecked()) {
            setLanguage(true);
        } else {
            setLanguage(false);
        }

        Intent i = new Intent(this, MainMenuActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * This method sets language preferences true = AppTheme1, false = AppTheme
     */
    public void setTheme(Boolean style) {
        // retrieve preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // stablish a writer
        SharedPreferences.Editor editor = prefs.edit();
        // redefine style flag
        editor.putBoolean("estilo", style);
        editor.commit();
    }

    /**
     * This method updates the devices localization to maintain consistence with os calls display language
     *
     * @param newLocal
     */
    public void updateLocalization(String newLocal) {
        // change the localization of the device to maintain consistence in the OS routines language
        Locale nuevaloc = new Locale(newLocal);
        // stablish the new localization
        Locale.setDefault(nuevaloc);
        // retrieve config OS prefs
        Configuration config = new Configuration();
        // set the new localization
        config.locale = nuevaloc;
        // update localization
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    /**
     * This method sets language preferences true = english , false = spanish
     */
    public void setLanguage(Boolean lang) {
        // retrieve preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // stablish a writer
        SharedPreferences.Editor editor = prefs.edit();
        // redefine language flag
        editor.putBoolean("ingles", lang);
        // set changes
        editor.commit();
        if (lang) {
            // localization english
            updateLocalization("en");
        } else {
            // localization spanish
            updateLocalization("es");
        }
    }
}
