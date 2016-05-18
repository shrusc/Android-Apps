package shruti.cs.sdsu.edu.assignment1;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;

public class MainActivity extends ActionBarActivity {
    String msg = "Assignment 1 : ";
    TextView textView;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(msg, "onCreate()");
        textView = (TextView) this.findViewById(R.id.textView);
        text = getResources().getString(R.string.onCreate);
        textView.append(text);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(msg, "onStart()");
        text = getResources().getString(R.string.onStart);
        textView.append(text);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(msg, "onRestart()");
        text = getResources().getString(R.string.onRestart);
        textView.append(text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(msg, "onResume()");
        text = getResources().getString(R.string.onResume);
        textView.append(text);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(msg, "onPause()");
        text = getResources().getString(R.string.onPause);
        textView.append(text);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(msg, "onSaveInstanceState()");
        text = getResources().getString(R.string.onSaveInstanceState);
        textView.append(text);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(msg, "onRestoreInstanceState()");
        text = getResources().getString(R.string.onRestoreInstanceState);
        textView.append(text);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(msg, "onStop()");
        text = getResources().getString(R.string.onStop);
        textView.append(text);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(msg, "onDestroy()");
        text = getResources().getString(R.string.onDestroy);
        textView.append(text);
    }


    public void clearPressed(View button) {
        textView.setText(" ");
    }

}
