package com.example.findmyrhythm.View;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.content.Intent;

import com.example.findmyrhythm.R;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;

import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
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

        //Login with google
       /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        signInButton.setClickable(true);
        signInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GreetingsActivity.class);
                startActivity(intent);

            }
        });
        TextView textView = (TextView) signInButton.getChildAt(0);
        //signInButton.setSize(SignInButton.SIZE_STANDARD);
        textView.setText("Continuar con Google");


        //Login with Facebook
        //FacebookSdk.sdkInitialize(getApplicationContext());
       // mCallbackManager = CallbackManager.Factory.create();

        LoginButton mFacebookSignInButton = (LoginButton) findViewById(R.id.login_button_facebook);
        mFacebookSignInButton.setClickable(true);
        mFacebookSignInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GreetingsActivity.class);
                startActivity(intent);

            }
        });
        //mFacebookSignInButton.setReadPermissions("email", "public_profile", "user_birthday", "user_friends");

       /* mFacebookSignInButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        // Initialize FirebaseAuth
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();*/


    }

    @Override
    public void onClick(View view) {

    }

    /*@Override
    protected void onStart()//Se ejecuta después del create
    {

        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account!=null){
            //Pasar directamente a su pantalla de perfil sin necesidad de loguearse
        }
        //FirebaseUser currentUser = mAuth.getCurrentUser();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;

        }
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:falló,código=" + e.getStatusCode());
        }
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
                            Log.d(TAG, "Éxito en el acceso mediante Google");
                            FirebaseUser currentUser = task.getResult().getUser();
                            //final String name = currentUser.getDisplayName();
                            //final String email = currentUser.getEmail();
                            //final Uri photoUrl = currentUser.getPhotoUrl();

                            Toast.makeText(Login.this, getString(R.string.authExi),  Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, Profile.class);
                            startActivity(intent);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "Fallo en el acceso mediante Google");
                            Toast.makeText(Login.this, getString(R.string.authFail),  Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Fallo en el acceso mediante Facebook", task.getException());
                            Toast.makeText(Login.this, getString(R.string.authFail),
                                    Toast.LENGTH_SHORT).show();


                        } else {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Éxito en el acceso mediante Facebook");
                            Toast.makeText(Login.this, getString(R.string.authExi),  Toast.LENGTH_SHORT).show();
                            FirebaseUser currentUser = task.getResult().getUser();
                            Intent intent = new Intent(Login.this, Profile.class);
                            startActivity(intent);



                        }
                    }
                });
    }*/
}