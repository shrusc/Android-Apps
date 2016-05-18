package edu.sdsu.cs.shruti.assignment3;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.UnsupportedEncodingException;

/**
 * A simple {@link Fragment} subclass
 * Use the {@link FeedBackFragmentTab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedBackFragmentTab extends Fragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "id";
    private String id;
    private HttpClient httpclient;
    private RequestQueue queue;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FeedBackFragmentTab.
     */
    public static FeedBackFragmentTab newInstance(String param1) {
        FeedBackFragmentTab fragment = new FeedBackFragmentTab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public FeedBackFragmentTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(
                R.layout.fragment_feed_back_fragment_tab, container, false);
        view.findViewById(R.id.postButton).setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        httpclient = new DefaultHttpClient();
    }

    @Override
    public void onPause() {
        super.onPause();
        httpclient.getConnectionManager().shutdown();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void onClick(View v) {
        String comment;
        EditText commentText = (EditText)getActivity().findViewById(R.id.commentEditText);
        comment = commentText.getText().toString();
        RatingBar bar = (RatingBar) getActivity().findViewById(R.id.ratingBar);
        String rating = Float.toString(bar.getRating());
        if(comment.equals("") && rating.equals("0")) {
            Toast.makeText(getActivity(), "Please provide a feedback",
                    Toast.LENGTH_LONG).show();
        }
        if(!rating.equals("0")) {
            String url = "http://bismarck.sdsu.edu/rateme/rating/"+id+"/"+rating;
            new PostRatingAsyncTask().execute(url);
        }
        if(!comment.equals("")) {
            String commentPostUrl = "http://bismarck.sdsu.edu/rateme/comment/"+id;
            new PostCommentAsyncTask().execute(commentPostUrl,comment);
        }
        closeKeyboard(getActivity(),getActivity().findViewById(R.id.commentEditText).getWindowToken());
    }

    public static void closeKeyboard(Context context, IBinder windowToken) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(windowToken, 0);
    }

    private class PostRatingAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... args) {
            try {
                HttpPost postMethod = new HttpPost(args[0]);
                try {
                    httpclient.execute(postMethod);
                } catch (Throwable t) {
                    Log.i("Assignment 3", t.toString());
                }
            }
            catch(Throwable t) {
                t.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            RatingBar bar = (RatingBar) getActivity().findViewById(R.id.ratingBar);
            bar.setRating(0);
            //if there are no comments display Toast
            EditText commentText = (EditText)getActivity().findViewById(R.id.commentEditText);
            if(commentText.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Your FeedBack is taken!! Thank you!!",
                        Toast.LENGTH_LONG).show();
            }
            //invalidating the cache so that the updates are seen
            String url = "http://bismarck.sdsu.edu/rateme/instructor/" + id;
            queue = Volley.newRequestQueue(getActivity());
            queue.getCache().invalidate(url, true);
        }
    }

    private class PostCommentAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... args) {
            try {
                HttpPost postMethod = new HttpPost(args[0]);
                postMethod.setHeader("Content-Type", "application/json;charset=UTF-8");
                try {
                    postMethod.setEntity(new StringEntity(args[1]));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    httpclient.execute(postMethod);
                } catch (Throwable t) {
                    Log.i("Assignment 3", t.toString());
                }
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            EditText commentText = (EditText)getActivity().findViewById(R.id.commentEditText);
            commentText.setText("");
            Toast.makeText(getActivity(), "Your FeedBack is taken!! Thank you!!",
                    Toast.LENGTH_LONG).show();
            //invalidating the cache so that the updates are seen
            String url = "http://bismarck.sdsu.edu/rateme/comments/" + id;
            queue = Volley.newRequestQueue(getActivity());
            queue.getCache().invalidate(url, true);

        }
    }
}
