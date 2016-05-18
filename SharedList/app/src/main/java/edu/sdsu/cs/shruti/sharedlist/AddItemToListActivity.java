package edu.sdsu.cs.shruti.sharedlist;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.parse.ParseObject;
import com.parse.ParseUser;


public class AddItemToListActivity extends ActionBarActivity {

    private EditText itemName;
    private EditText quantityValue;
    private String listName;
    private Boolean listIsShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_list);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        listName = getIntent().getStringExtra("listName");
        listIsShared = getIntent().getExtras().getBoolean("listIsShared");
        setTitle(listName);
        itemName = (EditText) findViewById(R.id.itemName);
        quantityValue = (EditText) findViewById(R.id.quantityValue);
        if(savedInstanceState!=null)
        {
            itemName.setText(savedInstanceState.getString("itemName"));
            quantityValue.setText(savedInstanceState.getString("quantity"));
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        outState.putString("itemName", itemName.getText().toString());
        outState.putString("quantity", quantityValue.getText().toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save_item_to_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.saveItem) {
            ParseObject listItem = new ParseObject("singleList");
            listItem.put("listName",listName);
            listItem.put("itemName",itemName.getText().toString());
            listItem.put("quantity",quantityValue.getText().toString());
            listItem.put("user", ParseUser.getCurrentUser().getUsername());
            if(listIsShared)
                listItem.put("shared","YES");
            else
                listItem.put("shared","NO");
            listItem.saveInBackground();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
