package com.me.taylor.gradpad;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    protected Button fb_button;
    protected Button email_button;
    protected Button phone_button;
    private View mProgressView;
    private View mLoginFormView;
    private CallbackManager  callbackManager;
    private TextView mStatusTextView;
    private Button signOut;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // UI
        fb_button = (Button) findViewById(R.id.login_button);
        email_button = (Button) findViewById(R.id.email_button);
        phone_button = findViewById(R.id.phone_button);
        mStatusTextView = findViewById(R.id.mStatusTextView);
        signOut = findViewById(R.id.sign_out);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);


        // Facebook

        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final boolean isLoggedIn = accessToken != null && !accessToken.isExpired();


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Toast.makeText(getApplicationContext(), "Canceled",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(getApplicationContext(),"Error" + exception,Toast.LENGTH_SHORT).show();

                    }
                });

        email_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent LoginIntent = new Intent(LoginActivity.this, LoginActivityEmail.class);
                LoginActivity.this.startActivity(LoginIntent);
                LoginActivity.this.finish();
            }
        });
        signOut.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                signOut();
                updateUI(mAuth.getCurrentUser());
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//    }



    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        // [START_EXCLUDE silent]
        showProgress(true);
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            showProgress(false);
                            Toast.makeText(LoginActivity.this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            showProgress(false);
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }
    // [END auth_with_facebook]


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


//    @Override
//    protected  void onStop(){
//        super.onStop();
//        LoginManager.getInstance().logOut();
//        signOut();
//    }

    protected  void signOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Show current user and sign out button
            String DISPLAY = user.getDisplayName();
            try {
                if (DISPLAY.equals(null)) {
                    DISPLAY = user.getEmail();
                }
            }
            catch(Exception exception){
                DISPLAY = user.getEmail();
                }


            signOut.setVisibility(View.VISIBLE);
            mStatusTextView.setVisibility(View.VISIBLE);
            mStatusTextView.setText(DISPLAY + " is signed in");

            // Hide sign in options
            fb_button.setVisibility(View.GONE);
            phone_button.setVisibility(View.GONE);
            email_button.setVisibility(View.GONE);
           // findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);

           // findViewById(R.id.verify_email_button).setEnabled(!user.isEmailVerified());
        } else {
            // Hide
            mStatusTextView.setVisibility(View.GONE);
            signOut.setVisibility(View.GONE);

            // Show
            email_button.setVisibility(View.VISIBLE);
            fb_button.setVisibility(View.VISIBLE);
            phone_button.setVisibility(View.VISIBLE);
        }
    }

}

