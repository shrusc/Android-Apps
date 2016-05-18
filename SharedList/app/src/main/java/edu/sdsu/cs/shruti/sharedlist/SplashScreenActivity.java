package edu.sdsu.cs.shruti.sharedlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.parse.ParseUser;

import java.util.Timer;
import java.util.TimerTask;


public class SplashScreenActivity extends ActionBarActivity {

    // Set Duration of the Splash Screen
    private final long Delay = 800;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the view from splash_screen.xml
        setContentView(R.layout.activity_splash_screen);

        // Create a Timer
        Timer runSplash = new Timer();

        // Task to do when the timer ends
        TimerTask showSplash = new TimerTask() {
            @Override
            public void run() {
                // Close SplashScreenActivity.class
                finish();

                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    // do stuff with the user
                    Intent intent = new Intent(getApplicationContext(),ListsActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        // Start the timer
        runSplash.schedule(showSplash, Delay);
    }
}
