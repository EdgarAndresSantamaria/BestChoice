package ehu.das.bestchoice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class AddDuelActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_add_duel);
        setListeners();
    }

    /**
     * Añade la funcionalidad a los botones
     */
    private void setListeners() {
        Button buttonPlay = findViewById(R.id.buttonAddDuel);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataIsValid()){
                    addDuel();
                }
            }
        });
    }

    /**
     * Con los datos del usuario llama a la función de crear un duelo
     */
    private void addDuel() {
        Spinner categorySpinner = findViewById(R.id.spinnerAddDuelCategory);
        int category = categorySpinner.getSelectedItemPosition() + 1;

        TextView first_option = findViewById(R.id.editTextAddDuelOption1);
        String name1 = first_option.getText().toString();

        TextView second_option = findViewById(R.id.editTextAddDuelOption2);
        String name2 = second_option.getText().toString();

        DatabaseWebService service = new DatabaseWebService(this);
        String[] args = {
                "createDuel",
                GeneralService.getUsername(),
                name1,
                name2,
                String.valueOf(category)
        };
        service.execute(args);

        // Creamos una notificación avisando de que suma 5 puntos
        NotificationManager manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "duelcreated",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(getString(R.string.notification_channel));
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, "1");
        builder.setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setSubText(getString(R.string.notification_extra))
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setAutoCancel(true);

        manager.notify(1, builder.build());

        finish();
    }

    /**
     *  Devuelve true si los campos no tienen caracteres no deseados
     * @return
     */
    private boolean dataIsValid(){
        TextView first_option = findViewById(R.id.editTextAddDuelOption1);
        String name1 = first_option.getText().toString();

        TextView second_option = findViewById(R.id.editTextAddDuelOption2);
        String name2 = second_option.getText().toString();

        if((!name1.matches("^[a-zA-Z0-9\\s]*$")) || name1.length() <1){
            first_option.setError(getString(R.string.error_bad_characters));
            first_option.requestFocus();
            return false;
        }else if ((!name2.matches("^[a-zA-Z0-9\\s]*$"))|| name2.length() <1){
            second_option.setError(getString(R.string.error_bad_characters));
            second_option.requestFocus();
            return false;
        }
        return true;
    }
}
