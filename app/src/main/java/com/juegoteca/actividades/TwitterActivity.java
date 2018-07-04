package com.juegoteca.actividades;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.mijuegoteca.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class TwitterActivity extends Activity {

    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        TwitterConfig config = new TwitterConfig.Builder(this)
                .twitterAuthConfig(new TwitterAuthConfig("HxyV7qaJl2YoWkdbOBN0HPwmc", "p4EQ0Y6xNGnjLgdyR6hubYSlMfIHj5TMB6TM6FShCe6VqWMKXM"))
                .build();
        Twitter.initialize(config);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_config);
        getActionBar().setDisplayHomeAsUpEnabled(true);


        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls

                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();

                //Registramos el token y el secret
                final SharedPreferences settings = getSharedPreferences("UserInfo",
                        0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("twitter_token", authToken.token);
                editor.putString("twitter_secret", authToken.secret);
                editor.commit();

                SharedPreferences globalSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                globalSettings.edit().putBoolean("twitter", true).commit();



                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.share_twitter_login_ok), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
                toast.show();

                Intent intent = new Intent(getApplicationContext(), Opciones.class);
                startActivity(intent);


            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast toast = Toast.makeText(getApplicationContext(),getString(R.string.share_twitter_login_ko)
                        , Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {
        SharedPreferences globalSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        globalSettings.edit().putBoolean("twitter", false).commit();
        Intent intent = new Intent(getApplicationContext(), Opciones.class);
        startActivity(intent);
        //super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                SharedPreferences globalSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                globalSettings.edit().putBoolean("twitter", false).commit();
                Intent intent = new Intent(getApplicationContext(), Opciones.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
