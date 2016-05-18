package edu.sdsu.cs.shruti.assignment2;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


public class MainActivity extends ActionBarActivity implements OnItemSelectedListener,DesertListFragment.OnFragmentInteractionListener {
    private EditText textView;
    private int listItemSelected = -1;
    private String spinnerItemSelected;
    private static final int INTENT_DATE_REQUEST = 123;
    private static final int INTENT_LIST_ITEM_REQUEST = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        textView = (EditText) this.findViewById(R.id.textView);
        textView.clearFocus();
        FragmentManager fragments = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragments.beginTransaction();
        DesertListFragment fragment = new DesertListFragment();
        fragmentTransaction.add(R.id.fragment_holder, fragment);
        fragmentTransaction.commit();
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
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // On selecting a spinner item
        spinnerItemSelected = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }

    public void goPressed(View view) {
        if(spinnerItemSelected.equals("Select a date")){
            Intent intent = new Intent(this,DateActivity.class);
            startActivityForResult(intent, INTENT_DATE_REQUEST);
        }
        else if(spinnerItemSelected.equals("See how the keyboard works")){
            Intent intent = new Intent(this,KeyboardActivity.class);
            intent.putExtra("text", textView.getText().toString());
            startActivity(intent);
        }
        else if(spinnerItemSelected.equals("All about list fragments")){
            Intent intent = new Intent(this,ListActivity.class);
            intent.putExtra("position",listItemSelected);
            startActivityForResult(intent, INTENT_LIST_ITEM_REQUEST);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != INTENT_DATE_REQUEST && requestCode != INTENT_LIST_ITEM_REQUEST) {
            return;
        }
        switch (resultCode) {
            case RESULT_OK:
                switch(requestCode) {
                    case INTENT_DATE_REQUEST:
                        String dateFromPicker = data.getStringExtra("date");
                        textView.setText(dateFromPicker);
                        break;
                    case INTENT_LIST_ITEM_REQUEST:
                        DesertListFragment newDesertListFragment ;
                        newDesertListFragment = DesertListFragment.newInstance(data.getIntExtra("position", -1));
                        // Execute a transaction, replacing the existing list fragment with a new instance
                        FragmentTransaction ft = getFragmentManager()
                                .beginTransaction();
                        ft.replace(R.id.fragment_holder, newDesertListFragment);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();

                }
            case RESULT_CANCELED:
                break;
        }
    }

    public void onFragmentInteraction(int index) {
        listItemSelected = index;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        Intent intent;
        switch (item.getItemId()) {
            case R.id.date:
                intent = new Intent(this,DateActivity.class);
                startActivityForResult(intent, INTENT_DATE_REQUEST);
                return true;
            case R.id.keyboard:
                intent = new Intent(this,KeyboardActivity.class);
                intent.putExtra("text", textView.getText().toString());
                startActivity(intent);
                return true;
            case R.id.list:
                intent = new Intent(this,ListActivity.class);
                intent.putExtra("position",listItemSelected);
                startActivityForResult(intent, INTENT_LIST_ITEM_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


}
