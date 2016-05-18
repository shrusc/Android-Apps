package edu.sdsu.cs.shruti.assignment3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {
    private ListView instructorsView;
    private ArrayList<HashMap<String,String>> instructorIdMap;
    private SQLiteDB databaseHelper;
    private RequestQueue queue;
    private Cache.Entry cachedData;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new SQLiteDB(MainActivity.this);
        instructorsView = (ListView) findViewById(R.id.listView);
        instructorIdMap = new ArrayList<HashMap<String,String>>();
        // listening to single list item on click
        instructorsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String selectedInstructor = ((TextView) view).getText().toString();
                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getApplicationContext(), InstructorDetailActivity.class);
                for (HashMap<String, String> hashMap : instructorIdMap) {
                    if(hashMap.get("fullName").equals(selectedInstructor)){
                        i.putExtra("id", hashMap.get("id"));
                    }
                }
                startActivity(i);
            }
        });
    }

    public void onResume() {
       super.onResume();
       if(isNetworkAvailable()) {
            getAllInstructorData();
        }
        else {
            new GetInstructorListFromDatabaseAsyncTask().execute();
        }
    }

    public void onPause() {
        super.onPause();
        instructorIdMap.clear();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void getAllInstructorData() {
        queue = Volley.newRequestQueue(this);
        String url = "http://bismarck.sdsu.edu/rateme/list";
        cachedData = queue.getCache().get(url);
        if (cachedData != null ) {
            try {
                JSONArray cacheData = new JSONArray(new String(cachedData.data, "UTF8"));
                parseJSONData(cacheData);
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
                public void onResponse(JSONArray response) {
                    parseJSONData(response);
                    progressDialog.dismiss();
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Log.d("Assignment 3", error.toString());
                    progressDialog.dismiss();
                }
            };
            JsonArrayRequest getRequest = new JsonArrayRequest( url, success, failure);
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Fetching The Data....");
            progressDialog.show();
            queue.add(getRequest);
        }

    }
    
    public void parseJSONData(JSONArray data) {
        new InsertInstructorListToDatabaseAsyncTask().execute(data);
        for (int i=0; i < data.length(); i++) {
            try {
                JSONObject object = data.getJSONObject(i);
                String id = object.getString("id");
                String firstName = object.getString("firstName");
                String lastName = object.getString("lastName");
                String fullName = String.valueOf(firstName) + " " + String.valueOf(lastName);
                HashMap<String, String> instructor = new HashMap<String, String>();
                instructor.put("id", id);
                instructor.put("fullName", fullName);
                instructorIdMap.add(instructor);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, instructorIdMap,
                android.R.layout.simple_list_item_1,
                new String[] {"fullName"},
                new int[]{android.R.id.text1});
        instructorsView.setAdapter(adapter);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private class InsertInstructorListToDatabaseAsyncTask extends AsyncTask<JSONArray, Void, Void> {
        @Override
        protected Void doInBackground(JSONArray... data) {
            try {
                databaseHelper.deleteAll();
                JSONArray instructors = data[0];
                for (int i=0; i < instructors.length(); i++) {
                    JSONObject object = instructors.getJSONObject(i);
                    String id = object.getString("id");
                    String firstName = object.getString("firstName");
                    String lastName = object.getString("lastName");
                    String fullName = String.valueOf(firstName) + " " + String.valueOf(lastName);
                    databaseHelper.insertInstructor(id, fullName);
                }
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    private class GetInstructorListFromDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... data) {
            try {
                instructorIdMap = (ArrayList<HashMap<String, String>>) databaseHelper.getAllInstructors();
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, instructorIdMap,
                    android.R.layout.simple_list_item_1,
                    new String[] {"fullName"},
                    new int[]{android.R.id.text1});
            instructorsView.setAdapter(adapter);

        }
    }

}
