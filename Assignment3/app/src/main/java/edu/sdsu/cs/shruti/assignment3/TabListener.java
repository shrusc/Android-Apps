package edu.sdsu.cs.shruti.assignment3;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class TabListener<T extends Fragment>
        implements ActionBar.TabListener {

    private Fragment fragment;
    private Bundle idArgsBundle;
    private ActionBarActivity parentActivity;

    public TabListener(Fragment fragment,Bundle args, ActionBarActivity activity) {
        this.fragment = fragment;
        this.idArgsBundle = args;
        this.parentActivity = activity;
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
        fragment.setArguments(idArgsBundle);
        if(tab.getText()=="Rating") {

            fragmentTransaction.attach(fragment);
        }
        else {
            fragmentTransaction.replace(R.id.container, fragment, null);

        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
        if (fragment != null) {
            View target = fragment.getView().findFocus();
            if (target != null)
            {
                InputMethodManager mgr = (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(target.getWindowToken(), 0);
            }
            fragmentTransaction.remove(fragment);
        }
    }
}
