package edu.sdsu.cs.shruti.sharedlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;

@SuppressWarnings("deprecation")
public class ListsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setTitle("SharedLists");
        Tab myListsTab = actionBar.newTab();
        Tab sharedListsTab = actionBar.newTab();

        MyListsFragment myListsFragment = new MyListsFragment();
        myListsTab.setText("My Lists")
                .setIcon(R.mipmap.ic_icon_list)
                .setContentDescription("The first tab")
                .setTabListener(
                        new ListTabListener<MyListsFragment>(
                                myListsFragment,null));

        SharedListsFragment sharedListsFragment = new SharedListsFragment();
        sharedListsTab
                .setText("Shared Lists")
                .setIcon(R.mipmap.ic_icon_shared_list)
                .setContentDescription("The second tab")
                .setTabListener(
                        new ListTabListener<SharedListsFragment>(
                                sharedListsFragment,null));

        actionBar.addTab(myListsTab);
        actionBar.addTab(sharedListsTab);

        if (savedInstanceState != null) {
            // get the saved selected tab's index and set that tab as selected
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tabIndex", 0));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //save the selected tab's index so it's re-selected on orientation change
        savedInstanceState.putInt("tabIndex", getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logOut) {
            ParseUser.logOut();
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
