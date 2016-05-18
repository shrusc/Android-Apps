package edu.sdsu.cs.shruti.assignment2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;


public class DateActivity extends ActionBarActivity {
    public static final String PREFS_NAME = "DatePrefsFile";
    private int year;
    private int month;
    private int day;
    private DatePicker datePicker;
    private SharedPreferences prefs;
    private String stringDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        prefs= getSharedPreferences(PREFS_NAME, 0);
        if(prefs.contains("year") && prefs.contains("month") && prefs.contains("day")){
            datePicker.updateDate(prefs.getInt("year",-1), prefs.getInt("month",-1), prefs.getInt("day",-1));
        }
        else {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            datePicker.init(year, month, day, null);
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
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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
                Intent goHome = getIntent();
                goHome.putExtra("date", stringDate);
                goHome.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                setResult(RESULT_OK, goHome);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectPressed(View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to choose this date?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stringDate = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
                        prefs= getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("year", datePicker.getYear());
                        editor.putInt("month", datePicker.getMonth());
                        editor.putInt("day", datePicker.getDayOfMonth());
                        editor.commit();
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }

    public void backPressed(View view){
        Intent toPassBack = getIntent();
        toPassBack.putExtra("date", stringDate);
        setResult(RESULT_OK, toPassBack);
        finish();
    }

}
