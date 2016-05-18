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
import com.parse.SignUpCallback;


public class SignUpActivity extends ActionBarActivity {

    private EditText signUpUserName;
    private EditText signUpPassword;
    private EditText signUpConfirmPassword;
    private String userNameText;
    private String passwordText;
    private String confirmPasswordText;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        signUpUserName = (EditText) findViewById(R.id.signUpUserName);
        signUpPassword = (EditText) findViewById(R.id.signUpPassword);
        signUpConfirmPassword = (EditText) findViewById(R.id.signUpConfirmPassword);
        if(savedInstanceState!=null)
        {
            signUpUserName.setText(savedInstanceState.getString("signUpUserName"));
            signUpPassword.setText(savedInstanceState.getString("signUpPassword"));
            signUpConfirmPassword.setText(savedInstanceState.getString("signUpConfirmPassword"));
        }
        Button signUpUser = (Button) findViewById(R.id.signUpUser);
        progressDialog = new ProgressDialog(this);
        signUpUserName.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        signUpUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                userNameText = signUpUserName.getText().toString();
                passwordText = signUpPassword.getText().toString();
                confirmPasswordText = signUpConfirmPassword.getText().toString();
                // Force user to fill up the form
                if (userNameText.equals("") && passwordText.equals("") && confirmPasswordText.equals(""))
                    Toast.makeText(getApplicationContext(), "Please complete the sign up form", Toast.LENGTH_LONG).show();
                else if (!passwordText.equals(confirmPasswordText))
                    Toast.makeText(getApplicationContext(), "Both the passwords entered do not match", Toast.LENGTH_LONG).show();
                else {
                    progressDialog.setMessage("Signing Up....");
                    progressDialog.show();
                    ParseUser.logInInBackground(userNameText, passwordText, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "You are already signed up. Please go back and Login", Toast.LENGTH_LONG).show();
                            } else {
                                ParseUser newUser = new ParseUser();
                                newUser.setUsername(userNameText);
                                newUser.setPassword(passwordText);
                                newUser.signUpInBackground(new SignUpCallback() {
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(
                                                    getApplicationContext(),
                                                    ListsActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Sign up Error", Toast.LENGTH_LONG).show();
                                            switch (e.getCode()) {
                                                case ParseException.INVALID_EMAIL_ADDRESS:
                                                    Toast.makeText(getApplicationContext(), "invalid email Error", Toast.LENGTH_LONG).show();
                                                    break;
                                                case ParseException.USERNAME_TAKEN:
                                                    Toast.makeText(getApplicationContext(), "USERNAME_TAKEN", Toast.LENGTH_LONG).show();
                                                    break;
                                                case ParseException.EMAIL_TAKEN:
                                                    Toast.makeText(getApplicationContext(), "EMAIL_TAKEN Error", Toast.LENGTH_LONG).show();
                                                    break;
                                                default:
                                                    Toast.makeText(getApplicationContext(), "default Error", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                });

                            }
                        }
                    });

                }
            }
        });
    }


    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        outState.putString("signUpUserName", signUpUserName.getText().toString());
        outState.putString("signUpPassword", signUpPassword.getText().toString());
        outState.putString("signUpConfirmPassword", signUpConfirmPassword.getText().toString());
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
