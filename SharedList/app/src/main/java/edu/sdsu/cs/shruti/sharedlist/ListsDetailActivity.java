package edu.sdsu.cs.shruti.sharedlist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class ListsDetailActivity extends ActionBarActivity {
    private String listName;
    private List<String> listItemDetails;
    private List<String> listQuantityDetails;
    private ListView listDetailView;
    private CustomItemsInListAdapter listViewAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists_detail);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching The Items....");
        progressDialog.show();
        Intent i = getIntent();
        listName = i.getStringExtra("listName");
        listItemDetails = new ArrayList<>();
        listQuantityDetails = new ArrayList<>();
        listViewAdapter=new CustomItemsInListAdapter(this, listItemDetails.toArray(new String[listItemDetails.size()]),
                listQuantityDetails.toArray(new String[listQuantityDetails.size()]),R.mipmap.ic_action_put_in_50);
        listDetailView = (ListView) findViewById(R.id.listDetailView);
        setTitle(listName);
        registerForContextMenu(listDetailView);
    }

    @Override
    public void onResume() {
        super.onResume();
        listItemDetails.clear();
        listQuantityDetails.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("singleList");
        query.whereEqualTo("listName", listName);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < result.size(); i++) {
                        ParseObject object = result.get(i);
                        listItemDetails.add(object.getString("itemName"));
                        listQuantityDetails.add(object.getString("quantity"));
                    }
                    listDetailView.setAdapter(listViewAdapter);
                    listViewAdapter.refreshList(listItemDetails.toArray(new String[listItemDetails.size()]),
                            listQuantityDetails.toArray(new String[listQuantityDetails.size()]));
                    listViewAdapter.notifyDataSetChanged();
                } else {
                    Log.d("SharedList", "Error: " + e.getMessage());
                }
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        progressDialog.dismiss();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                deleteListItem(listItemDetails.get(info.position));
                listItemDetails.remove(info.position);
                listQuantityDetails.remove(info.position);
                listViewAdapter.refreshList(listItemDetails.toArray(new String[listItemDetails.size()]),
                        listQuantityDetails.toArray(new String[listQuantityDetails.size()]));
                listViewAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    void deleteListItem(String listItemName) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("singleList");
        query.whereEqualTo("listName",listName);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lists_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.shareList) {
            final EditText input = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("Share List")
                    .setMessage("Enter group Name")
                    .setView(input)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String name = input.getText().toString();
                            if (name.equals(""))
                                Toast.makeText(getApplicationContext(), "Please type in a name", Toast.LENGTH_LONG).show();
                            else {
                                Intent i = new Intent(getApplicationContext(), AddMemberToListActivity.class);
                                i.putExtra("sharedListName", listName);
                                i.putExtra("groupName",name);
                                startActivity(i);
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
        if (id == R.id.addItemToList) {
            // Set an EditText view to get user input
            Intent i = new Intent(this, AddItemToListActivity.class);
            i.putExtra("listName", listName);
            i.putExtra("listIsShared",false);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
