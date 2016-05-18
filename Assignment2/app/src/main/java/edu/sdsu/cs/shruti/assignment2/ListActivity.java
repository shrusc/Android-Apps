package edu.sdsu.cs.shruti.assignment2;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class ListActivity extends ActionBarActivity implements DesertListFragment.OnFragmentInteractionListener{
    private int listItemSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager fragments = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragments.beginTransaction();
        DesertListFragment fragment = new DesertListFragment();
        if(getIntent().getExtras() != null)
            fragment.setArguments(getIntent().getExtras());
        fragmentTransaction.add(R.id.fragment_holder, fragment);
        fragmentTransaction.commit();
    }

    public void backPressed(View view) {
        Intent toPassBack = getIntent();
        toPassBack.putExtra("position", listItemSelected);
        setResult(RESULT_OK, toPassBack);
        finish();
    }

    public void onFragmentInteraction(int index) {
        listItemSelected = index;
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
                goHome.putExtra("position", listItemSelected);
                goHome.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                setResult(RESULT_OK, goHome);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
