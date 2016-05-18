package edu.sdsu.cs.shruti.assignment2;

import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class DesertListFragment extends ListFragment implements AbsListView.OnItemClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "position";

    private OnFragmentInteractionListener mListener;
    private String[] deserts ;
    private int selectedItem = -1;
    private View selectedView = null;
    private Boolean firstStart = true;
    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
    private ArrayAdapter<String> adapter;

    public static DesertListFragment newInstance(int index) {
        DesertListFragment fragment = new DesertListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, index);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DesertListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            selectedItem = getArguments().getInt("position");
        }
        deserts = getActivity().getResources().getStringArray(R.array.deserts);
        ArrayList<String> desertList = new ArrayList<String>();
        desertList.addAll( Arrays.asList(deserts) );
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, desertList)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                final View renderer = super.getView(position, convertView, parent);
                if (firstStart && position == selectedItem)
                {
                    renderer.setBackgroundResource(android.R.color.darker_gray);
                }
                else
                    renderer.setBackgroundResource(Color.TRANSPARENT);
                return renderer;
            }
        };


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item3, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(adapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        return view;
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
                adapter.remove(adapter.getItem(info.position));
                adapter.notifyDataSetChanged();
                return true;
            case R.id.edit:
                Toast.makeText(getActivity(), "edit pressed", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
              mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        registerForContextMenu(getListView());
    }

    @Override
    public void onListItemClick (ListView l, View v, int position, long id){
        if (firstStart) {
            // first time  highlight selected row
            selectedView = l.getChildAt(selectedItem);
        }
        firstStart = false;
        if (selectedView != null && selectedView != v) {
            selectedView.setBackgroundColor(Color.TRANSPARENT);
        }
        selectedView = v;
        selectedView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        if (null != mListener) {
            mListener.onFragmentInteraction(position);
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(position);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(int id);
    }

}
