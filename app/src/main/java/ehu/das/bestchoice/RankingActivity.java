package ehu.das.bestchoice;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class RankingActivity extends AppCompatActivity {
    private ListView ranking;
    private String[] usernames;
    private int[] points;

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
        setContentView(R.layout.activity_ranking);

        ranking = (ListView) findViewById(R.id.listViewRanking);

        if (savedInstanceState != null) {
            usernames = savedInstanceState.getStringArray("usernames");
            points = savedInstanceState.getIntArray("points");

            RankingListViewAdapter adapter = new RankingListViewAdapter(getApplicationContext(), usernames, points);
            ranking.setAdapter(adapter);
        } else {
            getRanking();
        }
    }

    /**
     * Llama al servicio para obtener el ranking
     */
    private void getRanking() {
        DatabaseWebService service = new DatabaseWebService(this);
        String[] args = {"getRanking"};
        service.execute(args);
    }

    /**
     * Crea el ranking
     *
     * @param jsonArray top 10
     */
    public void createRanking(JSONArray jsonArray) {
        int rankingSize = jsonArray.size();

        usernames = new String[rankingSize];
        points = new int[rankingSize];

        int i = 0;
        for (Object j : jsonArray) {
            JSONObject json = (JSONObject) j;
            try {
                String usr = json.get("username").toString();
                String pts = json.get("points").toString();

                usernames[i] = usr;
                points[i] = Integer.parseInt(pts);

                i++;
            } catch (NullPointerException e) {
                Log.d("duel", "RankingActivity CREATE " + e.getMessage());
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RankingListViewAdapter adapter = new RankingListViewAdapter(getApplicationContext(), usernames, points);
                ranking.setAdapter(adapter);
            }
        });
    }


    /**
     * Guarda los datos en un bundle.
     *
     * @param savedInstanceState
     */
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArray("usernames", usernames);
        savedInstanceState.putIntArray("points", points);
    }

}
