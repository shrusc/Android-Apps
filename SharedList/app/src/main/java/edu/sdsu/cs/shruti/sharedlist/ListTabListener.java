package edu.sdsu.cs.shruti.sharedlist;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;


public class ListTabListener<T extends Fragment>
        implements ActionBar.TabListener {

    private final Fragment fragment;
    private final Bundle idArgsBundle;

    public ListTabListener(Fragment fragment,Bundle args) {
        this.fragment = fragment;
        this.idArgsBundle = args;
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        fragment.setArguments(idArgsBundle);
        fragmentTransaction.replace(R.id.container, fragment, null);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
    }
}