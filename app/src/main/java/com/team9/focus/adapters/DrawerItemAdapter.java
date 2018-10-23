package com.team9.focus.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team9.focus.R;

/**
 * Created by jtsui on 9/29/17.
 */

public class DrawerItemAdapter extends ArrayAdapter {
    Context mContext;
    int layoutResourceId;
    String data[] = null;

    public DrawerItemAdapter(Context mContext, int layoutResourceId, String[] data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        //ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
        TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);

        //DataModel folder = data[position];


        //imageViewIcon.setImageResource(folder.icon);
        textViewName.setText(data[position]);

        return listItem;
    }
}
