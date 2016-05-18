package edu.sdsu.cs.shruti.assignment2;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class KeyboardActivity extends ActionBarActivity {
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editText = (EditText) this.findViewById(R.id.editText);
        if(getIntent().getExtras() != null)
            editText.setText(getIntent().getExtras().getString("text"));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void backPressed(View view) {
        Intent goHome = new Intent(this,MainActivity.class);
        boolean foundHome = navigateUpTo(goHome);
    }

    public void hidePressed(View view) {
        View currentFocusView = this.getCurrentFocus();
        if (currentFocusView != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        Intent intent;
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent goHome = new Intent(this,MainActivity.class);
                goHome.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(goHome);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
