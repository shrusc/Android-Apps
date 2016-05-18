package edu.sdsu.cs.shruti.assignment3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class InstructorDetailActivity extends ActionBarActivity  {


    private DetailsFragmentTab detailsFragment;
    private CommentsFragmentTab commentsFragment;
    private FeedBackFragmentTab feedBackFragment;
    private Tab detailsTab,commentsTab,feedBackTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_instructor_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(actionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        detailsTab = actionBar.newTab();
        commentsTab = actionBar.newTab();
        feedBackTab = actionBar.newTab();

        Intent i = getIntent();
        Bundle idBundle = new Bundle();
        idBundle.putString("id", i.getStringExtra("id"));

        detailsFragment = new DetailsFragmentTab();
        detailsTab.setText("Details")
                .setContentDescription("The first tab")
                .setTabListener(
                        new TabListener<DetailsFragmentTab>(
                                detailsFragment,idBundle,this));

        commentsFragment = new CommentsFragmentTab();
        commentsTab
                .setText("Comments")
                .setContentDescription("The second tab")
                .setTabListener(
                        new TabListener<CommentsFragmentTab>(
                                commentsFragment,idBundle,this));

        feedBackFragment = new FeedBackFragmentTab();
        feedBackTab
                .setText("FeedBack")
                .setContentDescription("The third tab")
                .setTabListener(
                        new TabListener<FeedBackFragmentTab>(
                                feedBackFragment,idBundle,this));


        actionBar.addTab(detailsTab);
        actionBar.addTab(commentsTab);
        actionBar.addTab(feedBackTab);

        if (savedInstanceState != null) {
            Log.i("ass 3", "setting selected tab from saved bundle");
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
