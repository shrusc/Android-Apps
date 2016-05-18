package edu.sdsu.cs.shruti.sharedlist;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SharedListItemsFragment extends Fragment {

    private String sharedListName;
    private List<String> sharedListItemsDetails;
    private List<String> sharedListQuantityDetails;
    private ListView sharedListItemsDetailView;
    private CustomItemsInListAdapter listViewAdapter;
    private ProgressDialog progressDialog;

    public SharedListItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sharedListName = getArguments().getString("sharedListName");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching The Items....");
        progressDialog.show();
        return inflater.inflate(R.layout.fragment_shared_list_items, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedListItemsDetails = new ArrayList<>();
        sharedListQuantityDetails = new ArrayList<>();
        sharedListItemsDetailView = (ListView) getActivity().findViewById(R.id.sharedListItemsView);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("singleList");
        query.whereEqualTo("listName", sharedListName);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < result.size(); i++) {
                        ParseObject object = result.get(i);
                        sharedListItemsDetails.add(object.getString("itemName"));
                        sharedListQuantityDetails.add(object.getString("quantity"));
                    }
                    listViewAdapter = new CustomItemsInListAdapter(getActivity(), sharedListItemsDetails.toArray(new String[sharedListItemsDetails.size()]),
                            sharedListQuantityDetails.toArray(new String[sharedListQuantityDetails.size()]),R.mipmap.ic_action_put_in_50);
                    sharedListItemsDetailView.setAdapter(listViewAdapter);
                } else {
                    Log.d("SharedLists", "Error: " + e.getMessage());
                }
                progressDialog.dismiss();
            }
        });
        registerForContextMenu(sharedListItemsDetailView);
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
                deleteSharedListItem(sharedListItemsDetails.get(info.position));
                sharedListItemsDetails.remove(info.position);
                sharedListQuantityDetails.remove(info.position);
                listViewAdapter.refreshList(sharedListItemsDetails.toArray(new String[sharedListItemsDetails.size()]),
                        sharedListQuantityDetails.toArray(new String[sharedListQuantityDetails.size()]));
                listViewAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    void deleteSharedListItem(String listItemName) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("singleList");
        query.whereEqualTo("listName",sharedListName);
        query.whereEqualTo("itemName",listItemName);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < result.size(); i++)
                        result.get(i).deleteInBackground();
                }
                else {
                    Log.d("SharedList", "Error: " + e.getMessage());
                }
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_add_item_to_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addItem) {
            // Set an EditText view to get user input
            Intent i = new Intent(getActivity(), AddItemToListActivity.class);
            i.putExtra("listName", sharedListName);
            i.putExtra("listIsShared",true);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
