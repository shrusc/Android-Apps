package edu.sdsu.cs.shruti.sharedlist;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class AddMemberToListActivity extends ActionBarActivity {

    private ListView contactsListView;
    private String sharedListName;
    private String groupName;
    private ArrayList<String> contactNumbers;
    private ArrayList<String> contactNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_to_list);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        contactsListView = (ListView) findViewById(R.id.contactsListView);
        contactNames = new ArrayList<>();
        contactNumbers = new ArrayList<>();
        sharedListName = getIntent().getStringExtra("sharedListName");
        groupName = getIntent().getStringExtra("groupName");
        setTitle(groupName);
        getContacts();
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, contactNames);
        contactsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        contactsListView.setAdapter(listViewAdapter);
        if (savedInstanceState != null) {
            int[] checkedItems = savedInstanceState.getIntArray("checkedItems");
            if (checkedItems != null) {
                for (int checkedItem : checkedItems) {
                    contactsListView.setItemChecked(checkedItem, true);
                }
            }
        }
    }

    private void getContacts(){
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{id}, null);
                    while (phoneCursor.moveToNext()) {
                        String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String displayName = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        contactNames.add(displayName);
                        contactNumbers.add(number);
                    }
                    phoneCursor.close();
                }
            }
        }
        cursor.close();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //check if any items are selected
        if (contactsListView.getCheckedItemCount() > 0) {
            //get the list of selected items and convert it to an int Array
            //because SparseBooleanArray cannot be stored in a bundle
            SparseBooleanArray selectedItems = contactsListView.getCheckedItemPositions();
            int[] selectedItemsArray = new int[contactsListView.getCheckedItemCount()];
            for (int i = 0; i < selectedItems.size(); i++) {
                if (!selectedItems.valueAt(i))
                    continue;
                selectedItemsArray[i] = selectedItems.keyAt(i);
            }
            outState.putIntArray("checkedItems", selectedItemsArray);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save_members_to_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.saveMembers) {
            SparseBooleanArray checked = contactsListView.getCheckedItemPositions();
            final ArrayList<String> selectedMembers = new ArrayList<>();
            selectedMembers.add(ParseUser.getCurrentUser().getUsername());
            for (int i = 0; i < checked.size(); i++) {
                // Item position in adapter
                int position = checked.keyAt(i);
                if (checked.valueAt(i))
                    selectedMembers.add(contactNumbers.get(position));
            }
            ParseQuery<ParseObject> sharedListQuery = ParseQuery.getQuery("sharedList");
            sharedListQuery.whereEqualTo("groupName", groupName);
            sharedListQuery.whereEqualTo("sharedListName", sharedListName);
            sharedListQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject result, ParseException e) {
                    if (e == null) {
                        List<String> sharedMembers = result.getList("memberList");
                        List<String> membersToAdd = new ArrayList<>(selectedMembers);
                        membersToAdd.removeAll(sharedMembers);
                        membersToAdd.addAll(sharedMembers);
                        result.put("memberList", membersToAdd);
                        result.saveInBackground();
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("singleList");
                        query.whereEqualTo("listName", sharedListName);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> result, ParseException e) {
                                if (e == null) {
                                    for (int i = 0; i < result.size(); i++) {
                                        result.get(i).put("shared", "YES");
                                        result.get(i).saveInBackground();
                                    }
                                } else {
                                    Log.d("SharedList", "Error: " + e.getMessage());
                                }
                                finish();
                            }
                        });
                    } else {
                        ParseObject list = new ParseObject("sharedList");
                        list.put("groupName", groupName);
                        list.put("sharedListName", sharedListName);
                        list.put("memberList", selectedMembers);
                        list.saveInBackground();
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("singleList");
                        query.whereEqualTo("listName", sharedListName);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> result, ParseException e) {
                                if (e == null) {
                                    for (int i = 0; i < result.size(); i++) {
                                        result.get(i).put("shared", "YES");
                                        result.get(i).saveInBackground();
                                    }
                                } else {
                                    Log.d("SharedList", "Error: " + e.getMessage());
                                }
                                finish();
                            }
                        });

                    }
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
