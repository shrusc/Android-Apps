package edu.sdsu.cs.shruti.sharedlist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class MyListsFragment extends Fragment {

    private ArrayList<String> myLists;
    private ListView myListsView;
    private CustomListAdapter listViewAdapter;
    private ProgressDialog progressDialog;

    public MyListsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching The Lists....");
        progressDialog.show();
        return inflater.inflate(R.layout.fragment_my_lists, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        myListsView = (ListView) getActivity().findViewById(R.id.myLists);
        myLists = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("singleList");
        query.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e) {
                if (e == null){
                    Boolean copy;
                    for (int i = 0; i < result.size(); i++) {
                        ParseObject object = result.get(i);
                        copy = true;
                        if(object.getString("shared").equals("YES"))
                            continue;
                        if(myLists.size() == 0) {
                            myLists.add(object.getString("listName"));
                            continue;
                        }
                        for(int j=0; j < myLists.size(); j++){
                            if(object.getString("listName").equals(myLists.get(j)))
                                copy = false;
                        }
                        if(copy)
                            myLists.add(object.getString("listName"));
                    }
                    if(getActivity()!=null) {
                        listViewAdapter=new CustomListAdapter(getActivity(), myLists.toArray(new String[myLists.size()]),
                                R.mipmap.ic_action_purchase_order_32);
                        myListsView.setAdapter(listViewAdapter);
                    }
                } else {
                    Log.d("SharedLists", "Error: " + e.getMessage());
                }
                progressDialog.dismiss();
            }
        });
        myListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedList = ((TextView) view.findViewById(R.id.listText)).getText().toString();
                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getActivity(), ListsDetailActivity.class);
                i.putExtra("listName", selectedList);
                startActivity(i);
            }
        });
        registerForContextMenu(myListsView);
    }

    @Override
    public void onPause() {
        super.onPause();
        myLists.clear();
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
                deleteList(myLists.get(info.position));
                myLists.remove(info.position);
                listViewAdapter.refreshList(myLists.toArray(new String[myLists.size()]));
                listViewAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    void deleteList(String listName) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("singleList");
        query.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("listName",listName);
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
        inflater.inflate(R.menu.menu_new_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.newList) {
            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            new AlertDialog.Builder(getActivity())
                .setTitle("New List")
                .setMessage("Enter list Name")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String name = input.getText().toString();
                        if (name.equals(""))
                            Toast.makeText(getActivity(), "Please type in a name", Toast.LENGTH_LONG).show();
                        else {
                            for (int i = 0; i < myLists.size(); i++) {
                                if (name.equals(myLists.get(i))) {
                                    Toast.makeText(getActivity(), "List already exists!! Type another name", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                            ParseQuery<ParseObject> sharedListQuery = ParseQuery.getQuery("sharedList");
                            sharedListQuery.whereEqualTo("memberList", ParseUser.getCurrentUser().getUsername());
                            sharedListQuery.whereEqualTo("sharedListName",name);
                            sharedListQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(getActivity(), "List already exists!! Type another name", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Intent i = new Intent(getActivity(), AddItemToListActivity.class);
                                        i.putExtra("listName", name);
                                        i.putExtra("listIsShared",false);
                                        startActivity(i);
                                    }

                                }
                            });
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
