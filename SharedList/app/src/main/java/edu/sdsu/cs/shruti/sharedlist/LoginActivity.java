package edu.sdsu.cs.shruti.sharedlist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginActivity extends ActionBarActivity {

    private EditText passWord;
    private EditText userName;
    private String userNameText;
    private String passwordText;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        userName = (EditText) findViewById(R.id.userName);
        passWord = (EditText) findViewById(R.id.password);
        if(savedInstanceState!=null)
        {
            userName.setText(savedInstanceState.getString("userName"));
            passWord.setText(savedInstanceState.getString("passWord"));
        }
        Button loginButton = (Button) findViewById(R.id.login);
        Button signUpButton = (Button) findViewById(R.id.signup);
        progressDialog = new ProgressDialog(this);
            userName.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    // Retrieve the text entered from the EditText
                    progressDialog.setMessage("Logging In....");
                    progressDialog.show();
                    userNameText = userName.getText().toString();
                    passwordText = passWord.getText().toString();
                    // Send data to Parse.com for verification
                    ParseUser.logInInBackground(userNameText, passwordText, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), ListsActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "No such user exist, please SignUp", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
            signUpButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                    startActivity(intent);
                }
            });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putString("userName", userName.getText().toString());
        outState.putString("passWord", passWord.getText().toString());
    }

}
