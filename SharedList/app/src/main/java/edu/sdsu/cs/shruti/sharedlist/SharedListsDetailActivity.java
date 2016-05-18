package edu.sdsu.cs.shruti.sharedlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;


@SuppressWarnings("deprecation")
public class SharedListsDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_lists_detail);
        Intent i = getIntent();
        Bundle sharedListNameBundle = new Bundle();
        sharedListNameBundle.putString("sharedListName", i.getStringExtra("sharedListName"));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setTitle(i.getStringExtra("sharedListName"));

        ActionBar.Tab itemsTab = actionBar.newTab();
        ActionBar.Tab membersTab = actionBar.newTab();

        SharedListItemsFragment sharedListItemsFragment = new SharedListItemsFragment();
        itemsTab.setText("Items")
                .setIcon(R.mipmap.ic_icon_items)
                .setContentDescription("The first tab")
                .setTabListener(
                        new ListTabListener<MyListsFragment>(
                                sharedListItemsFragment,sharedListNameBundle));

        SharedListMembersFragment sharedListMembersFragment = new SharedListMembersFragment();
        membersTab
                .setText("Members")
                .setIcon(R.mipmap.ic_icon_members)
                .setContentDescription("The second tab")
                .setTabListener(
                        new ListTabListener<SharedListsFragment>(
                                sharedListMembersFragment,sharedListNameBundle));

        actionBar.addTab(itemsTab);
        actionBar.addTab(membersTab);

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

}
