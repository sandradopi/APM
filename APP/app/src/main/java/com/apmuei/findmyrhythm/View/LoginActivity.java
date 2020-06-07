package com.apmuei.findmyrhythm.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.apmuei.findmyrhythm.Model.AttendeeService;
import com.apmuei.findmyrhythm.Model.Event;
import com.apmuei.findmyrhythm.Model.EventService;
import com.apmuei.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.apmuei.findmyrhythm.Model.IOFiles;
import com.apmuei.findmyrhythm.Model.Organizer;
import com.apmuei.findmyrhythm.Model.OrganizerService;
import com.apmuei.findmyrhythm.Model.PersistentOrganizerInfo;
import com.apmuei.findmyrhythm.Model.PersistentUserInfo;
import com.apmuei.findmyrhythm.Model.RatingService;
import com.apmuei.findmyrhythm.Model.User;
import com.apmuei.findmyrhythm.Model.UserService;
import com.apmuei.findmyrhythm.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    //private static final String NAME = "name";
    //private static final String EMAIL = "email";
    //private static final String PHOTO = "photo";
    private static final String TAG = "Login";
    GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Custom Layout for the Login
        setContentView(R.layout.activity_main);
        setTitle(R.string.titleLogin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        TextView textView = (TextView) signInButton.getChildAt(0);
        textView.setText(R.string.continue_with_google);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button_facebook);
        loginButton.setPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });


        /* / Initialize FirebaseAuth
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();*/

    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.authFail, Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();

        // TODO: delete. Test lines (2)
        //FirebaseAuth.getInstance().signOut();
        //googleSignOut();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            //TODO: Mirar si es organizador o usuario
            /*Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);*/
        }
    }


    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            IOFiles.downloadProfilePicture(currentUser, getApplicationContext());
            new CheckUserTask().execute(currentUser);
        }
    }


    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    /**
     * Sign out of your Google account.
     * Necessary to log in with a different account (accounts menu).
     */
    private void googleSignOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // ...
                }
            });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.authFail,
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                googleSignIn();
                break;
            // ...
        }
    }


    private class CheckUserTask extends AsyncTask<FirebaseUser, Void, Void> {
        User user;
        Organizer organizer;

        @Override
        protected Void doInBackground(FirebaseUser... fbUser) {
            // Check if user already signed-in previously

            try {
                UserService userService = new UserService();
                user = userService.getUser(fbUser[0].getUid());
            } catch (InstanceNotFoundException e) {
                user = null;
            }

            try {
                OrganizerService organizerService = new OrganizerService();
                organizer = organizerService.getOrganizer(fbUser[0].getUid());
            } catch (InstanceNotFoundException e) {
                organizer = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (user==null && organizer==null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                EulaDialog dialogo = new EulaDialog();
                dialogo.show(fragmentManager, "tagAlerta");
                //Intent intent = new Intent(LoginActivity.this, GreetingsActivity.class);
                //startActivity(intent);
            } else if (user!=null && organizer==null) {
                showUseExistentAccountDialog(user, organizer);
            } else if (user==null && organizer!=null) {
                showUseExistentAccountDialog(user, organizer);
            } else if (user!=null && organizer!=null) {
                showSelectAccountDialog(user, organizer);
            }
        }
    }


    private class RecoverUserTask extends AsyncTask<User, Void, Void> {

        ArrayList<Event> events;
        ArrayList<String> ratedEvents;

        @Override
        protected Void doInBackground(User... user) {

            AttendeeService attendeeService = new AttendeeService();
            events = attendeeService.getEventsByUser(user[0].getId());

            RatingService ratingService = new RatingService();
            ratedEvents = ratingService.getRatingsByUser(user[0].getId());


            PersistentUserInfo persistentUserInfo = new PersistentUserInfo(user[0].getId(),user[0].getName(),
                    user[0].getUsername(),user[0].getEmail(), user[0].getBiography(), user[0].getBirthdate(),
                    user[0].getSubscribedLocations(), user[0].getSubscribedGenres(), events, ratedEvents);

            PersistentUserInfo.setPersistentUserInfo(getApplicationContext(), persistentUserInfo);

            SharedPreferences sharedpreferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putString("fb_name", persistentUserInfo.getName());
            editor.putString("fb_email", persistentUserInfo.getEmail());
            editor.putString("fb_id", persistentUserInfo.getId());
            editor.putString("name", persistentUserInfo.getName());
            editor.putString("email", persistentUserInfo.getEmail());
            editor.putString("nickname", persistentUserInfo.getUsername());
            editor.putString("account_type", "user");

            editor.commit(); // or apply

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Intent intent = new Intent(LoginActivity.this, UserProfileActivity.class);
            // Flags for start a new activity and clear all stack
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }


    private class RecoverOrganizerTask extends AsyncTask<Organizer, Void, Void> {

        ArrayList<Event> events;

        @Override
        protected Void doInBackground(Organizer... organizer) {

            EventService organizerService = new EventService();
            events = organizerService.findEventByOrganicer(organizer[0].getId());

            PersistentOrganizerInfo persistentOrganizerInfo = new PersistentOrganizerInfo(organizer[0].getId(),
                    organizer[0].getName(), organizer[0].getUsername(), organizer[0].getEmail(),
                    organizer[0].getBiography(), organizer[0].getRating(), organizer[0].getLocation(), events);

            PersistentOrganizerInfo.setPersistentOrganizerInfo(getApplicationContext(), persistentOrganizerInfo);

            SharedPreferences sharedpreferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putString("fb_name", persistentOrganizerInfo.getName());
            editor.putString("fb_email", persistentOrganizerInfo.getEmail());
            editor.putString("fb_id", persistentOrganizerInfo.getId());
            editor.putString("name", persistentOrganizerInfo.getName());
            editor.putString("email", persistentOrganizerInfo.getEmail());
            editor.putString("nickname", persistentOrganizerInfo.getUsername());
            editor.putString("location", persistentOrganizerInfo.getLocation());
            editor.putString("account_type", "organizer");

            editor.commit(); // or apply

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Intent intent = new Intent(LoginActivity.this, OrganizerProfileActivity.class);
            // Flags for start a new activity and clear all stack
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }


    public void showSelectAccountDialog(final User user, final Organizer organizer) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.select);
        alertDialog.setMessage(getString(R.string.two_accounts));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.usuario_log),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new RecoverUserTask().execute(user);
                    }
                });


        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.organicer_log),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new RecoverOrganizerTask().execute(organizer);
                    }
                });
        alertDialog.show();
    }


    public void showUseExistentAccountDialog(final User user, final Organizer organizer) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        final String account = (organizer != null) ? "organizer" : "user";
        final String noaccount = (organizer != null) ? "user" : "organizer";

        alertDialog.setTitle(R.string.account_exist);
        alertDialog.setMessage(getString(R.string.account_previous) + account + getString(R.string.account_use));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sí",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (organizer != null)
                            new RecoverOrganizerTask().execute(organizer);
                        else
                            new RecoverUserTask().execute(user);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.account_create) + noaccount,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(noaccount.equals("organizer")){
                            Intent intent = new Intent(LoginActivity.this, OrganizerLogActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(LoginActivity.this, SetLocationActivity.class);
                            startActivity(intent);
                        }
                        //Intent intent = new Intent(LoginActivity.this, GreetingsActivity.class);
                        //startActivity(intent);
                    }
                });
        alertDialog.show();
    }


}
