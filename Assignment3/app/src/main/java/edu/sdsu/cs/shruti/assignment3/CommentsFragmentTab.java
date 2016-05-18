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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link CommentsFragmentTab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentsFragmentTab extends Fragment {
    private static final String ARG_PARAM1 = "id";
    private String id;
    private List<HashMap<String, String>> instructorComments;
    private SQLiteDB databaseHelper;
    private RequestQueue queue;
    private Cache.Entry cachedData;
    private ProgressDialog progressDialog;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @return A new instance of fragment CommentsFragmentTab.
     */
    public static CommentsFragmentTab newInstance(String id) {
        CommentsFragmentTab fragment = new CommentsFragmentTab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, id);
        fragment.setArguments(args);
        return fragment;
    }

    public CommentsFragmentTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ARG_PARAM1);
        }
        databaseHelper = new SQLiteDB(getActivity());
        instructorComments = new ArrayList<HashMap<String, String>>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(
                R.layout.fragment_comments_fragment_tab, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isNetworkAvailable()) {
            getInstructorComments();
        }
        else {
            new GetInstructorCommentsFromDatabaseAsyncTask().execute();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    public void getInstructorComments() {
        queue = Volley.newRequestQueue(getActivity());
        String url = "http://bismarck.sdsu.edu/rateme/comments/" + id;
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
                    Log.d("Assignment 3","Error from getting comments" + error.toString());
                    progressDialog.dismiss();
                }
            };
            JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Fetching The Comments....");
            progressDialog.show();
            queue.add(getRequest);
        }
    }

    public void parseJSONData(JSONArray data) {
        new InsertInstructorCommentsToDatabaseAsyncTask().execute(data);
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject row = data.getJSONObject(i);
                HashMap<String, String> comment = new HashMap<String, String>();
                comment.put("title", row.getString("text"));
                comment.put("subtitle", row.getString("date"));
                instructorComments.add(comment);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        ListView commentsList = (ListView) getActivity().findViewById(R.id.commentsListView);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), instructorComments,
                android.R.layout.simple_list_item_2,
                new String[]{"title", "subtitle"},
                new int[]{android.R.id.text1,
                        android.R.id.text2});
        commentsList.setAdapter(adapter);

    }

    private class InsertInstructorCommentsToDatabaseAsyncTask extends AsyncTask<JSONArray, Void, Void> {
        @Override
        protected Void doInBackground(JSONArray... data) {

            try {
                databaseHelper.deleteInstructorComments(id);
                JSONArray comments = data[0];
                for (int i = 0; i < comments.length(); i++) {
                    JSONObject row = comments.getJSONObject(i);
                    databaseHelper.insertInstructorComments(id, row.getString("text"), row.getString("date"));
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

    private class GetInstructorCommentsFromDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... data) {
            try {
                instructorComments = databaseHelper.getAllInstructorComments(id);
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            ListView commentsList = (ListView)getActivity().findViewById(R.id.commentsListView);
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), instructorComments,
                    android.R.layout.simple_list_item_2,
                    new String[] {"title", "subtitle"},
                    new int[] {android.R.id.text1,
                            android.R.id.text2});
            commentsList.setAdapter(adapter);

        }
    }

}
