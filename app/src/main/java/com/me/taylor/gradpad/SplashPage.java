package com.me.taylor.gradpad;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.FacebookActivity;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class SplashPage extends AppCompatActivity {
    protected final int DISPLAY_LENGTH = 2000;    //  Length of time before transition to next screen


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);


        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);



        //  After the display length time is over, new intent is started (transition)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create the Intent that will start the Menu-Activity
                Intent LoginIntent = new Intent(SplashPage.this, LoginActivity.class);
                SplashPage.this.startActivity(LoginIntent);
                SplashPage.this.finish();
            }
        }, DISPLAY_LENGTH);
    }
}
