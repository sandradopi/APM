package com.example.findmyrhythm.View;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.findmyrhythm.Model.AttendeeService;
import com.example.findmyrhythm.Model.Event;
import com.example.findmyrhythm.Model.EventService;
import com.example.findmyrhythm.Model.Exceptions.InstanceNotFoundException;
import com.example.findmyrhythm.Model.Organizer;
import com.example.findmyrhythm.Model.OrganizerService;
import com.example.findmyrhythm.Model.PersistentOrganizerInfo;
import com.example.findmyrhythm.Model.PersistentUserInfo;
import com.example.findmyrhythm.Model.User;
import com.example.findmyrhythm.Model.UserService;
import com.example.findmyrhythm.R;
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
        textView.setText("Continuar con Google");

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
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
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
            //addNewUser(currentUser);
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

            if (user==null && organizer==null) {
                Intent intent = new Intent(LoginActivity.this, GreetingsActivity.class);
                startActivity(intent);
            } else if (user!=null && organizer==null) {
                recoverUser(user);
            } else if (user==null && organizer!=null) {
                recoverOrganizer(organizer);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (user!=null && organizer!=null) {
                showSelectAccountDialog(user, organizer);
            }
        }
    }

    public void recoverUser(User user) {
        AttendeeService attendeeService = new AttendeeService();
        ArrayList<Event> events = attendeeService.getEventsByUser(user.getId());

        PersistentUserInfo persistentUserInfo = new PersistentUserInfo(user.getId(),user.getName(),
                user.getUsername(),user.getEmail(), user.getBiography(), user.getBirthdate(),
                user.getSubscribedLocations(), user.getSubscribedGenres(), events,
                new ArrayList<Event>());

        PersistentUserInfo.setPersistentUserInfo(getApplicationContext(), persistentUserInfo);

        Intent intent = new Intent(LoginActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }

    public void recoverOrganizer(Organizer organizer) {
        OrganizerService organizerService = new OrganizerService();
        AttendeeService attendeeService = new AttendeeService();
        ArrayList<Event> events = organizerService.getOrganizedEventsByOrganizer(organizer.getId());

        PersistentOrganizerInfo persistentOrganizerInfo = new PersistentOrganizerInfo(organizer.getId(),
                organizer.getName(), organizer.getUsername(),organizer.getEmail(),
                organizer.getBiography(), organizer.getRating(), organizer.getLocation(), events);

        PersistentOrganizerInfo.setPersistentOrganizerInfo(getApplicationContext(), persistentOrganizerInfo);

        Intent intent = new Intent(LoginActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }

    public void showSelectAccountDialog(final User user, final Organizer organizer) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Selecciona tu cuenta");
        alertDialog.setMessage("Se han detectado dos cuentas previas: una de usuario y otra de organizador, ¿cuál quieres utilizar?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Usuario",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        recoverUser(user);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ninguna",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, GreetingsActivity.class);
                        startActivity(intent);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Organizador",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        recoverOrganizer(organizer);
                    }
                });
        alertDialog.show();
    }

}
