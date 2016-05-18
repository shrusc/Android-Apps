package edu.sdsu.cs.shruti.sharedlist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class SharedListsFragment extends Fragment {
    private ArrayList<String> sharedLists;
    private ListView sharedListsView;
    private CustomListAdapter listViewAdapter;
    private ProgressDialog progressDialog;

    public SharedListsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching The Lists....");
        progressDialog.show();
        return inflater.inflate(R.layout.fragment_shared_lists, container, false);
    }


    @Override
    public void onPause() {
        super.onPause();
        sharedLists.clear();
        progressDialog.dismiss();
    }


    @Override
    public void onResume() {
        super.onResume();
        sharedListsView = (ListView) getActivity().findViewById(R.id.sharedLists);
        sharedLists = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("sharedList");
        query.whereEqualTo("memberList", ParseUser.getCurrentUser().getUsername());
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < result.size(); i++) {
                        ParseObject object = result.get(i);
                        sharedLists.add(object.getString("sharedListName"));
                    }
                    if(getActivity()!=null) {
                        listViewAdapter = new CustomListAdapter(getActivity(), sharedLists.toArray(new String[sharedLists.size()]),
                                R.mipmap.ic_action_purchase_order_32);
                        sharedListsView.setAdapter(listViewAdapter);
                    }
                } else {
                    Log.d("SharedLists", "Error: " + e.getMessage());
                }
                progressDialog.dismiss();
            }
        });
        sharedListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sharedListName = ((TextView) view.findViewById(R.id.listText)).getText().toString();
                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getActivity(), SharedListsDetailActivity.class);
                i.putExtra("sharedListName", sharedListName);
                startActivity(i);
            }
        });
        registerForContextMenu(sharedListsView);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                deleteSharedList(sharedLists.get(info.position));
                sharedLists.remove(info.position);
                listViewAdapter.refreshList(sharedLists.toArray(new String[sharedLists.size()]));
                listViewAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    void deleteSharedList(String sharedListName) {
        ParseQuery<ParseObject> sharedListQuery = ParseQuery.getQuery("sharedList");
        sharedListQuery.whereEqualTo("memberList", ParseUser.getCurrentUser().getUsername());
        sharedListQuery.whereEqualTo("sharedListName",sharedListName);
        sharedListQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < result.size(); i++)
                        result.get(i).deleteInBackground();
                }
                else {
                    Log.d("SharedLists", "Error: " + e.getMessage());
                }
            }
        });
        ParseQuery<ParseObject> singleListQuery = ParseQuery.getQuery("singleList");
        singleListQuery.whereEqualTo("listName",sharedListName);
        singleListQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < result.size(); i++)
                        result.get(i).deleteInBackground();
                }
                else {
                    Log.d("SharedLists", "Error: " + e.getMessage());
                }
            }
        });

    }

}
