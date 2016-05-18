package edu.sdsu.cs.shruti.sharedlist;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
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

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class SharedListMembersFragment extends Fragment {

    private String sharedListName;
    private String groupName;
    private List<String> sharedListMemberDetails;
    private ListView sharedListMemberDetailView;
    private CustomListAdapter listViewAdapter;
    private List<String>memberList;
    private ProgressDialog progressDialog;

    public SharedListMembersFragment() {
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
        progressDialog.setMessage("Fetching The Members....");
        progressDialog.show();
        return inflater.inflate(R.layout.fragment_shared_list_members, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        sharedListMemberDetails = new ArrayList<>();
        sharedListMemberDetailView = (ListView) getActivity().findViewById(R.id.sharedListMembersView);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("sharedList");
        query.whereEqualTo("sharedListName", sharedListName);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("SharedList", "Error" + e.getMessage());
                } else {
                    memberList = object.getList("memberList");
                    groupName = object.getString("groupName");
                    for(int i=0; i < memberList.size(); i++) {
                        if(memberList.get(i).equals(ParseUser.getCurrentUser().getUsername()))
                            sharedListMemberDetails.add("You");
                        else {
                            String name = getContactName(getActivity(), memberList.get(i));
                            if (null == name)
                                sharedListMemberDetails.add(memberList.get(i));
                            else
                                sharedListMemberDetails.add(name);
                        }
                    }
                    listViewAdapter = new CustomListAdapter(getActivity(),
                            sharedListMemberDetails.toArray(new String[sharedListMemberDetails.size()]),
                            R.mipmap.ic_action_user_male3_50);
                    sharedListMemberDetailView.setAdapter(listViewAdapter);
                }
                progressDialog.dismiss();
            }
        });
        registerForContextMenu(sharedListMemberDetailView);
    }

    private static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
        }
        cursor.close();
        return contactName;
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
                deleteSharedListMember(memberList.get(info.position));
                sharedListMemberDetails.remove(info.position);
                listViewAdapter.refreshList(sharedListMemberDetails.toArray(new String[sharedListMemberDetails.size()]));
                listViewAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    void deleteSharedListMember(String memberUserName) {
        memberList.remove(memberUserName);
        ParseQuery<ParseObject> sharedListQuery = ParseQuery.getQuery("sharedList");
        sharedListQuery.whereEqualTo("groupName", groupName);
        sharedListQuery.whereEqualTo("sharedListName", sharedListName);
        sharedListQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject result, ParseException e) {
                if (e == null) {
                    result.put("memberList", memberList);
                    result.saveInBackground();
                } else {
                    Log.d("SharedList", "Error: " + e.getMessage());
                }
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_add_member_to_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addMember) {
            // Set an EditText view to get user input
            Intent i = new Intent(getActivity(), AddMemberToListActivity.class);
            i.putExtra("sharedListName", sharedListName);
            i.putExtra("groupName",groupName);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
