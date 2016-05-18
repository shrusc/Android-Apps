package edu.sdsu.cs.shruti.assignment3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragmentTab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragmentTab extends Fragment {
    private static final String ARG_PARAM1 = "id";
    private String id;
    private List<HashMap<String, String>> instructorInfo;
    private SQLiteDB databaseHelper;
    private RequestQueue queue;
    private Cache.Entry cachedData;
    private ProgressDialog progressDialog;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment DetailsFragmentTab.
     */
    public static DetailsFragmentTab newInstance(String param1) {
        DetailsFragmentTab fragment = new DetailsFragmentTab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailsFragmentTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ARG_PARAM1);
        }
        databaseHelper = new SQLiteDB(getActivity());
        instructorInfo = new ArrayList<HashMap<String, String>>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details_fragment_tab, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
       if(isNetworkAvailable()) {
            getInstructorDetailedData();
       }
        else {
           new GetDetailsFromDatabaseAsyncTask().execute();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getInstructorDetailedData() {
        queue = Volley.newRequestQueue(getActivity());
        String url = "http://bismarck.sdsu.edu/rateme/instructor/" + id;
        cachedData = queue.getCache().get(url);
        if ( cachedData != null ) {
            try {
                JSONObject cacheData = new JSONObject(new String(cachedData.data, "UTF8"));
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
            Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
                public void onResponse(JSONObject response) {
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
            JsonObjectRequest getRequest = new JsonObjectRequest(url, null, success, failure);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Fetching The Details....");
            progressDialog.show();
            queue.add(getRequest);
        }
    }

    public void parseJSONData(JSONObject data) {
        new InsertDetailsToDatabaseAsyncTask().execute(data);
        try {
            String firstName = data.getString("firstName");
            String lastName = data.getString("lastName");
            String fullName = String.valueOf(firstName) + " " + String.valueOf(lastName);
            HashMap<String, String> instructorName = new HashMap<String, String>(2);
            instructorName.put("title", "Name");
            instructorName.put("subtitle",fullName);
            instructorInfo.add(instructorName);
            Iterator<String> keysIterator = data.keys();
            while (keysIterator.hasNext()) {
                HashMap<String, String> instructorDetails = new HashMap<String, String>(2);
                String keyStr =  keysIterator.next();
                if (!keyStr.equals("rating") && !keyStr.equals("firstName") && !keyStr.equals("lastName")) {
                    String valueStr = data.getString(keyStr);
                    instructorDetails.put("title", keyStr);
                    instructorDetails.put("subtitle", valueStr);
                    instructorInfo.add(instructorDetails);
                }
            }
            JSONObject instructorRating = data.getJSONObject("rating");
            HashMap<String, String> avgRating = new HashMap<String, String>(2);
            avgRating.put("title","Average Rating");
            avgRating.put("subtitle",instructorRating.getString("average"));
            instructorInfo.add(avgRating);
            HashMap<String, String> totalRating = new HashMap<String, String>(2);
            totalRating.put("title","Total Rating");
            totalRating.put("subtitle",instructorRating.getString("totalRatings"));
            instructorInfo.add(totalRating);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        ListView detailsList = (ListView)getActivity().findViewById(R.id.detailsListView);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), instructorInfo,
                android.R.layout.simple_list_item_2,
                new String[] {"title", "subtitle"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
        detailsList.setAdapter(adapter);

    }

    private class InsertDetailsToDatabaseAsyncTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject...data) {

            try {
                JSONObject details = data[0];
                databaseHelper.deleteInstructorDetails(id);
                String firstName = details.getString("firstName");
                String lastName = details.getString("lastName");
                String fullName = String.valueOf(firstName) + " " + String.valueOf(lastName);
                JSONObject instructorRating = details.getJSONObject("rating");
                databaseHelper.insertInstructorDetails(details.getString("id"), fullName,
                        details.getString("office"), details.getString("phone"),
                        details.getString("email"), instructorRating.getString("average"),
                        instructorRating.getString("totalRatings"));
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

    private class GetDetailsFromDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void...data) {
            try {
                instructorInfo = databaseHelper.getAllInstructorDetails(id);
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            ListView detailsList = (ListView)getActivity().findViewById(R.id.detailsListView);
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), instructorInfo,
                    android.R.layout.simple_list_item_2,
                    new String[] {"title", "subtitle"},
                    new int[] {android.R.id.text1,
                            android.R.id.text2});
            detailsList.setAdapter(adapter);

        }
    }


}
