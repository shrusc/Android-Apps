package edu.sdsu.cs.shruti.sharedlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CustomItemsInListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private String[] listItems;
    private String[] listItemsQuantity;
    private final int imageId;

    public CustomItemsInListAdapter(Context context, String[] listItems,String[] listItemsQuantity,int imageId) {
        super(context, R.layout.list_item, listItems);
        this.context=context;
        this.listItems=listItems;
        this.imageId=imageId;
        this.listItemsQuantity=listItemsQuantity;
    }

    static class ViewHolder {
        public TextView text;
        public TextView subText;
        public ImageView image;
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            rowView = inflater.inflate(R.layout.list_content_item, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.listItemText);
            viewHolder.subText = (TextView) rowView.findViewById(R.id.listQuantityText);
            viewHolder.image = (ImageView) rowView.findViewById(R.id.listImage);
            rowView.setTag(viewHolder);
        }
        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        //String s = names[position];
        holder.text.setText(listItems[position]);
        holder.subText.setText(listItemsQuantity[position]);
        holder.image.setImageResource(imageId);
        return rowView;

    }

    public void refreshList(String[] newListItems,String[] newListItemsQuantity) {
        this.listItems = newListItems;
        this.listItemsQuantity = newListItemsQuantity;
    }
}
