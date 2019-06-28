package ehu.das.bestchoice;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DatabaseWebService extends AsyncTask<String, Void, Boolean> {
    private WeakReference<Context> contextRef;

    // Recibe el contexto para realizar callbacks a la actividad de origen
    DatabaseWebService(Context context) {
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected Boolean doInBackground(String... args) {
        Context context = contextRef.get();
        HttpURLConnection urlConnection;

        try {
            // Creamos la conexión con el servidor y establecemos los parámetros
            String URL = "https://134.209.235.115/mabad008/WEB/BestChoiceService.php";
            URL uri = new URL(URL);
            urlConnection = GeneradorConexionesSeguras.getInstance().crearConexionSegura(context, URL);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            // Creamos el objeto JSON con el que enviar los parámetros
            JSONObject json = new JSONObject();
            switch (args[0]) {
                case "getDuels":
                    // Enviamos la función y la categoria
                    json.put("function", args[0]);
                    json.put("category", args[1]);
                    break;
                case "voteDuel":
                    // Enviamos la función, el id del duelo, la opción que ha votado y el user (o no)
                    json.put("function", args[0]);
                    json.put("duel_id", args[1]);
                    json.put("option", args[2]);
                    if (GeneralService.userLogged()) {
                        json.put("user", GeneralService.getUsername());
                    } else {
                        json.put("user", "null");
                    }
                    break;
                case "getRanking":
                    // Envíamos la función
                    json.put("function", args[0]);
                    break;
                case "createDuel":
                    // Enviamos la función, usuario, opciones y categoría
                    json.put("function", args[0]);
                    json.put("user", args[1]);
                    json.put("name1", args[2]);
                    json.put("name2", args[3]);
                    json.put("category", args[4]);
                    break;
                case "getStats":
                    //Envíamos la función
                    json.put("function", args[0]);
                    break;
            }

            // Metemos los parámetros
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(json.toString());
            out.close();

            // Comprobamos si ha ido bien y recogemos el resultado
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                inputStream.close();
                JSONParser parser = new JSONParser();

                switch (args[0]) {
                    case "getDuels":
                        // Parsea el resultado a un JSONArray y llama a la función de crear duelos
                        JSONArray jsonArray = (JSONArray) parser.parse(result);
                        DuelActivity getDuelActivityContext = (DuelActivity) contextRef.get();
                        try {
                            getDuelActivityContext.createDuels(jsonArray);
                        } catch (Exception e) {
                            Log.e("duel", "DatabaseWebService RESPONSE " + e.getMessage());
                        }
                        break;
                    case "voteDuel":
                        // Parsea el resultado a JSONObject y con los votos llama a actualizar
                        JSONObject jsonResult = (JSONObject) parser.parse(result);
                        String votes_first = (String) jsonResult.get("votes1");
                        String votes_second = (String) jsonResult.get("votes2");
                        DuelActivity voteDuelActivityContext = (DuelActivity) contextRef.get();
                        try {
                            voteDuelActivityContext.updateVotes(votes_first, votes_second);
                        } catch (Exception e) {
                            Log.e("duel", "DatabaseWebService RESPONSE " + e.getMessage());
                        }
                        break;
                    case "getRanking":
                        // Parsea el resultado a un JSONArray y llama a la función de crear ranking
                        JSONArray jsonArrayRanking = (JSONArray) parser.parse(result);
                        RankingActivity getRankingActivityContext = (RankingActivity) contextRef.get();
                        try {
                            getRankingActivityContext.createRanking(jsonArrayRanking);
                        } catch (Exception e) {
                            Log.e("duel", "DatabaseWebService RESPONSE " + e.getMessage());
                        }
                        break;
                    case "getStats":
                        // Parsea el resultado a un JSONArray y llama a la función de crear stats
                        JSONArray jsonArrayStats = (JSONArray) parser.parse(result);
                        StatsActivity getStatsActivityContext = (StatsActivity) contextRef.get();
                        try {
                            getStatsActivityContext.createStats(jsonArrayStats);
                        } catch (Exception e) {
                            Log.e("duel", "DatabaseWebService RESPONSE " + e.getMessage());
                        }
                        break;
                }
            }
        } catch (Exception e) {
            Log.e("duel", "DatabaseWebService BBDD " + e.toString());
        }
        return true;
    }
}
