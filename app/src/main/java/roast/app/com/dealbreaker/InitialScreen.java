package roast.app.com.dealbreaker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import roast.app.com.dealbreaker.models.UpdatingQueueBranch;
import roast.app.com.dealbreaker.models.UpdatingViewingBranch;
import roast.app.com.dealbreaker.util.Constants;

public class InitialScreen extends AppCompatActivity {
    private Button registerButton,loginButton;
    private EditText mEmailEditText, mPasswordEditText;
    private TextView mLoginErrorMessage;
    private TextView mForgotPassword;

    Firebase userDatabase = new Firebase(Constants.FIREBASE_URL_USERS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("test");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FacebookSdk.sdkInitialize();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        registerButton = (Button) findViewById(R.id.register_button);
        loginButton = (Button) findViewById(R.id.login_button);
        mForgotPassword = (TextView) findViewById(R.id.forgotPasswordLink);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialScreen.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailEditText = (EditText) findViewById(R.id.LoginUsername);
                mPasswordEditText = (EditText) findViewById(R.id.LoginPassword);
                mLoginErrorMessage = (TextView) findViewById(R.id.loginErrorMessage);

                mLoginErrorMessage.setVisibility(View.INVISIBLE);
                mLoginErrorMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                final String username = mEmailEditText.getText().toString();
                final String userPassword = mPasswordEditText.getText().toString();

                final Firebase userDatabase = new Firebase(Constants.FIREBASE_URL_USERS);
                final Firebase ref = new Firebase(Constants.FIREBASE_URL);

                if(username.isEmpty()){
                    mLoginErrorMessage.setText("Username cannot be empty");
                    mLoginErrorMessage.setVisibility(View.VISIBLE);

                }
                else if(android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    mLoginErrorMessage.setText("Please enter username, not email.");
                    mLoginErrorMessage.setVisibility(View.VISIBLE);
                }
                else if(!isProperUserName(username)) {
                    mLoginErrorMessage.setText("Please enter a proper username.");
                    mLoginErrorMessage.setVisibility(View.VISIBLE);
                }
                else {
                    userDatabase.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            boolean usernameExists = snapshot.exists();

                            if (usernameExists && !username.isEmpty()) {

                                String userEmail = snapshot.child("email").getValue().toString();
                                System.out.println(userEmail);

                                ref.authWithPassword(userEmail, userPassword, new Firebase.AuthResultHandler() {
                                    @Override
                                    public void onAuthenticated(AuthData authData) {
                                        Intent intent = new Intent(InitialScreen.this, UserNavigation.class);
                                        intent.putExtra(getString(R.string.key_UserName), username);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onAuthenticationError(FirebaseError firebaseError) {
                                        // there was an error
                                        mLoginErrorMessage.setText("Username and password combination do not match. Please try again.");
                                        mLoginErrorMessage.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                mLoginErrorMessage.setText("Username and password combination do not match. Please try again.");
                                mLoginErrorMessage.setVisibility(View.VISIBLE);
                            }
                        }


                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                }
            }
        });

        mForgotPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialScreen.this, ResetPassword.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder exitAlertWindow = new AlertDialog.Builder(InitialScreen.this, R.style.AlertDialogTheme);
        exitAlertWindow.setTitle("Leave application?");
        exitAlertWindow.setMessage("Do you want to exit " + getString(R.string.app_name));
        exitAlertWindow.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                System.exit(0);
            }
        });
        exitAlertWindow.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        exitAlertWindow.show();
    }

    public Boolean isProperUserName(String username){
        for(int i = 0; i < username.length(); i++) {
            if( (Character.isLetter(username.charAt(i)))
                    || (Character.isDigit(username.charAt(i)))
                    ||  ((username.charAt(i)) == '_')
                    ||  ((username.charAt(i)) == '!') ){
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_initial_screen, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                Toast.makeText(this,"Information that may help you",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        mEmailEditText = (EditText) findViewById(R.id.LoginUsername);
        mPasswordEditText = (EditText) findViewById(R.id.LoginPassword);

        mEmailEditText.getText().clear();
        mPasswordEditText.getText().clear();
    }
}


