package ehu.das.bestchoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {
    // Keep track of the login task to ensure we can cancel it if requested.
    private UserLoginTask mAuthTask = null;

    // GUI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    /**
     * This method creates the GUI and ensembles the logic control for diferent views
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // retrieve user preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // check style preferences
        boolean style = prefs.getBoolean("estilo", false);

        // theme selection
        if (style) {
            // set presonalized style
            setTheme(R.style.AppTheme1);
        } else {
            // set default style
            setTheme(R.style.AppTheme);
        }

        // display GUI
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        // Set the password input
        mPasswordView = (EditText) findViewById(R.id.password);

        // ensemble the login logit to singInButton
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        // ensemble the logit to return button
        FloatingActionButton fab11 = (FloatingActionButton) findViewById(R.id.fab11);
        fab11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnMain();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // if not initialized
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String user = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // start control logic
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(user)) {
            // user slot was empty
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isUserValid(user)) {
            // user slot was wrong
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // if something was wrong cancel tryout
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserLoginTask(user, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * This method manages the back button use
     */
    @Override
    public void onBackPressed() {
        // return to main view
        returnMain();
    }

    /**
     * This method calls main activity and ends login activity
     */
    public void returnMain() {
        // return to main view 'MainActivity'
        Intent i = new Intent(this, MainMenuActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * This method checks the user length
     *
     * @param user username
     * @return boolean
     */
    private boolean isUserValid(String user) {
        return user.matches("^[a-zA-Z0-9]*$");
    }

    /**
     * This method checks wether password length is at least 8
     *
     * @param password password
     * @return boolean
     */
    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private String URL = "https://134.209.235.115/mabad008/WEB/gestorUsuarios.php";

        // values to intern control login attempt
        private final String mUser;
        private final String mPassword;
        private int mCod;
        public Boolean status;

        /**
         * Constructor for asynchronous login task
         *
         * @param user     user
         * @param password password
         */
        UserLoginTask(String user, String password) {
            // set up control values
            mUser = user;
            mPassword = password;
            mCod = -1;
        }

        /**
         * This method launches the verification / register task in background
         *
         * @param params params
         * @return result
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            HttpsURLConnection urlConnection = ehu.das.bestchoice.GeneradorConexionesSeguras.getInstance().crearConexionSegura(getApplicationContext(), URL);
            try {
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject parametrosJSON = new JSONObject();
                parametrosJSON.put("register", true);
                parametrosJSON.put("user", mUser);
                parametrosJSON.put("password", mPassword);

                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(parametrosJSON.toString());
                out.close();

                int statusCode = urlConnection.getResponseCode();
                if (statusCode == 200) {
                    BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String line, result = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    inputStream.close();

                    Log.w("response", result);
                    JSONObject json = new JSONObject(result);

                    String status = (String) json.get("estado");
                    int cod = (int) json.get("code");
                    if (status == "ok") {
                        return true;
                    } else {
                        mCod = cod;
                        return false;
                    }
                } else {
                    // error al realizar petición
                    Log.w("error", String.valueOf(statusCode));
                    mCod = 3;
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * This method displays an error if login attemp failed
         *
         * @param success success
         */
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (mCod == 200) {
                Log.w("login", "success");
                // if success update user preferences with the logged user
                GeneralService.setLogged(mUser);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user", mUser);
                editor.commit();
                //return to main view
                returnMain();
                finish();
            } else {
                Log.w("login", "not succed");
                Log.w("login", String.valueOf(mCod));
                // if something failed check what
                if (mCod == 0) {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                } else if (mCod == 1) {
                    mPasswordView.setError(getString(R.string.error_inexistent_user));
                } else if (mCod == 2) {
                    mPasswordView.setError(getString(R.string.error_bad_login_register));
                } else if (mCod == 3) {
                    mPasswordView.setError(getString(R.string.error_incorrect_call));
                } else if (mCod == 4) {
                    mPasswordView.setError(getString(R.string.error_server_error));
                }
                mPasswordView.requestFocus();
            }
        }

        /**
         * This method manages the situation that the user stops the login attempt
         */
        @Override
        protected void onCancelled() {
            // destroy login tadk object
            mAuthTask = null;
        }
    }
}
