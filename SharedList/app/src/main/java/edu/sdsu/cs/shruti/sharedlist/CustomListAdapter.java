package edu.sdsu.cs.shruti.sharedlist;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private String[] listItems;
    private final int imageId;

    static class ViewHolder {
        public TextView text;
        public ImageView image;
    }

    public CustomListAdapter(Activity context, String[] listItems,int imageId) {
        super(context, R.layout.list_item, listItems);
        this.context=context;
        this.listItems=listItems;
        this.imageId=imageId;
    }

    @Override
    public int getCount() {
        return listItems.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position,View rowView,ViewGroup parent) {
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.listText);
            viewHolder.image = (ImageView) rowView.findViewById(R.id.listImage);
            rowView.setTag(viewHolder);
        }
        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.text.setText(listItems[position]);
        holder.image.setImageResource(imageId);
        return rowView;

    }

    public void refreshList(String[] newListItems) {
        this.listItems = newListItems;
    }
}
