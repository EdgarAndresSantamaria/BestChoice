package ehu.das.bestchoice;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class StatsActivity extends AppCompatActivity {

    private String[] categories;
    private String[] names1;
    private String[] percentages1;
    private String[] names2;
    private String[] percentages2;

    private ListView stats;

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
        setContentView(R.layout.activity_stats);

        stats = (ListView) findViewById(R.id.listViewStats);
        if (savedInstanceState != null) {
            categories = savedInstanceState.getStringArray("categories");
            names1 = savedInstanceState.getStringArray("names1");
            percentages1 = savedInstanceState.getStringArray("percentages1");
            names2 = savedInstanceState.getStringArray("names2");
            percentages2 = savedInstanceState.getStringArray("percentages2");

            StatsListViewAdapter adapter = new StatsListViewAdapter(getApplicationContext(), categories, names1, percentages1, names2, percentages2);
            stats.setAdapter(adapter);
        } else {
            getStats();
        }
    }

    /**
     * Hace una petición para las estadísticas
     */
    private void getStats() {
        DatabaseWebService service = new DatabaseWebService(this);
        String[] args = {"getStats"};
        service.execute(args);
    }

    /**
     * Carga las estadísticas en el ListView
     *
     * @param jsonArray
     */
    public void createStats(JSONArray jsonArray) {
        int statsSize = jsonArray.size();

        categories = new String[statsSize];
        names1 = new String[statsSize];
        percentages1 = new String[statsSize];
        names2 = new String[statsSize];
        percentages2 = new String[statsSize];

        int i = 0;
        for (Object j : jsonArray) {
            JSONObject json = (JSONObject) j;
            try {
                int catIndex = Integer.parseInt(json.get("category").toString());
                String n1 = json.get("name1").toString();
                String votes1 = json.get("votes1").toString();
                String n2 = json.get("name2").toString();
                String votes2 = json.get("votes2").toString();

                categories[i] = GeneralService.getCategoryTag(catIndex, this);
                names1[i] = n1;
                names2[i] = n2;

                float first_votes = Float.parseFloat(votes1);
                float second_votes = Float.parseFloat(votes2);

                float first_perc = first_votes / (first_votes + second_votes) * 100;
                float second_perc = second_votes / (first_votes + second_votes) * 100;

                percentages1[i] = Math.round(first_perc * 10.0) / 10.0 + "%";
                percentages2[i] = Math.round(second_perc * 10.0) / 10.0 + "%";

                i++;
            } catch (NullPointerException e) {
                Log.d("duel", "StatsActivity CREATE " + e.getMessage());
            }

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    StatsListViewAdapter adapter = new StatsListViewAdapter(getApplicationContext(), categories, names1, percentages1, names2, percentages2);
                    stats.setAdapter(adapter);
                }

            });
        }
    }

    /**
     * Guarda los datos en memoria
     *
     * @param savedInstanceState
     */
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArray("categories", categories);
        savedInstanceState.putStringArray("names1", names1);
        savedInstanceState.putStringArray("percentages1", percentages1);
        savedInstanceState.putStringArray("names2", names2);
        savedInstanceState.putStringArray("percentages2", percentages2);
    }
}
