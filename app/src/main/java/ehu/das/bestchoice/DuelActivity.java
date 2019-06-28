package ehu.das.bestchoice;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DuelActivity extends AppCompatActivity {
    private DuelList duelList = new DuelList();
    private int category;
    private boolean hasVoted = false;

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
        setContentView(R.layout.activity_duel);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            category = extras.getInt("category");
        } else {
            category = 1;
        }

        setQuestion();
        setListeners();

        //Si hay duelos guardados los carga de la memoria
        if (savedInstanceState != null) {
            hasVoted = savedInstanceState.getBoolean("hasVoted");
            try{
                JSONParser parser = new JSONParser();
                JSONArray jsonArray = (JSONArray) parser.parse(savedInstanceState.getString("jsonArrayString"));
                createDuels(jsonArray);
            } catch (Exception e){
                Log.e("duel", "DuelActivity restoring duels: " + e.toString());
            }

            duelList.setCurrentDuelIndex(savedInstanceState.getInt("currentDuel"));
            updateCurrentDuel();

            if(hasVoted){
                Button buttonFirst = findViewById(R.id.button_duel_1);
                buttonFirst.setText(savedInstanceState.getString("text1"));
                buttonFirst.setOnClickListener(null);

                Button buttonSecond = findViewById(R.id.button_duel_2);
                buttonSecond.setText(savedInstanceState.getString("text2"));
                buttonSecond.setOnClickListener(null);
            }
        }else{ // Si no los hay, los carga del servidor
            getDuels();
        }

    }

    /**
     * Añade el texto a la pregunta del duelo
     */
    private void setQuestion() {
        TextView textQuestion = findViewById(R.id.text_duel_question);
        textQuestion.setText(GeneralService.getCategoryQuestion(category, this));
    }

    /**
     * Añade la funcionalidad a los botones
     */
    private void setListeners() {
        Button buttonFirst = findViewById(R.id.button_duel_1);
        buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vote(1);
            }
        });

        Button buttonSecond = findViewById(R.id.button_duel_2);
        buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vote(2);
            }
        });

        TextView buttonSkip = findViewById(R.id.button_duel_next);
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        TextView buttonBack = findViewById(R.id.button_duel_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    /**
     * Obtiene de la base de datos remota los duelos en función
     * de la categoría que ha seleccionado el usuario.
     */
    private void getDuels() {
        DatabaseWebService service = new DatabaseWebService(this);
        String[] args = {"getDuels", String.valueOf(category)};
        service.execute(args);
    }

    /**
     * Con los duelos obtenidos de base de datos crea los objetos
     * en la aplicación para poder trabajar con ellos.
     *
     * @param jsonArray lista de duelos en formato json
     */
    public void createDuels(JSONArray jsonArray) {
        for (Object j : jsonArray) {
            JSONObject json = (JSONObject) j;
            try {
                String id = json.get("id").toString();
                String name1 = json.get("name1").toString();
                String name2 = json.get("name2").toString();
                Duel duel = new Duel(id, name1, name2, category);
                duelList.addDuel(duel);
            } catch (NullPointerException e) {
                Log.d("duel", "DuelActivity CREATE " + e.getMessage());
            }
        }
        duelList.setJsonArrayString(jsonArray.toJSONString());
        updateCurrentDuel();
        //duelList.logDuels();
    }

    /**
     * Actualiza los botones con los nombres del duelo actual
     */
    void updateCurrentDuel() {
        Button buttonFirst = findViewById(R.id.button_duel_1);
        buttonFirst.setText(duelList.getCurrenDuel().getFirstName());

        Button buttonSecond = findViewById(R.id.button_duel_2);
        buttonSecond.setText(duelList.getCurrenDuel().getSecondName());
    }

    /**
     * Suma el voto en base de datos a la opción correspondiente.
     *
     * @param option opción seleccionada
     */
    private void vote(int option) {
        DatabaseWebService service = new DatabaseWebService(this);
        String[] args = {
                "voteDuel",
                duelList.getCurrenDuel().getID(),
                String.valueOf(option)
        };
        service.execute(args);
    }

    /**
     * Muestra en pantalla los porcentajes de votos de cada opción
     *
     * @param votes1 votos opción 1
     * @param votes2 votos opción 2
     */
    void updateVotes(String votes1, String votes2) {
        float first_votes = Float.parseFloat(votes1);
        float second_votes = Float.parseFloat(votes2);

        float first_perc = first_votes / (first_votes + second_votes) * 100;
        float second_perc = second_votes / (first_votes + second_votes) * 100;

        String v1 = first_perc + "%";
        String v2 = second_perc + "%";

        Button buttonFirst = findViewById(R.id.button_duel_1);
        buttonFirst.setText(v1);
        buttonFirst.setOnClickListener(null);

        Button buttonSecond = findViewById(R.id.button_duel_2);
        buttonSecond.setText(v2);
        buttonSecond.setOnClickListener(null);

        hasVoted = true;
    }

    /**
     * Pasa a la siguiente pregunta
     */
    private void next() {
        if (duelList.nextDuel()) {
            updateCurrentDuel();
            setListeners();
            hasVoted = false;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.duel_next_title));
            builder.setMessage(getString(R.string.duel_next_message));

            builder.setPositiveButton(getString(R.string.app_yes),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            goBackToMenu();
                        }
                    });
            builder.show();
        }
    }

    /**
     * Pregunta si quiere volver al menú principal y actua
     */
    private void back() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.duel_back_title));
        builder.setMessage(getString(R.string.duel_back_message));

        builder.setPositiveButton(getString(R.string.app_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goBackToMenu();
                    }
                });
        builder.show();
    }

    /**
     * Vuelve al menú principal
     */
    private void goBackToMenu() {
        finish();
    }

    /**
     * Guardar los datos al girar/salir del primer plano
     * @param savedInstanceState
     */
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("currentDuel", duelList.getCurrenDuelIndex());
        savedInstanceState.putBoolean("hasVoted", hasVoted);
        savedInstanceState.putString("jsonArrayString", duelList.getJsonArrayString());

        Button buttonFirst = findViewById(R.id.button_duel_1);
        savedInstanceState.putString("text1", (String) buttonFirst.getText());

        Button buttonSecond = findViewById(R.id.button_duel_2);
        savedInstanceState.putString("text2", (String) buttonSecond.getText());
    }



}
