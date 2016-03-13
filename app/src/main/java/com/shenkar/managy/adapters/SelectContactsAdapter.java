package com.shenkar.managy.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ex2.shenkar.todolist.ContactListItem;
import com.ex2.shenkar.todolist.R;
import com.ex2.shenkar.todolist.SelectContactDialog;

import java.util.ArrayList;

/**
 * Created by shnizle on 1/14/2016.
 */
public class SelectContactsAdapter extends BaseAdapter {

    private ArrayList<ContactListItem> contactListItems;
    private LayoutInflater mInflater;

    public SelectContactsAdapter(Context context, ArrayList<ContactListItem> contactListItems){

        this.contactListItems = contactListItems;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return contactListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return contactListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null){

            convertView = mInflater.inflate(R.layout.contant_list_item, null);
        }

        TextView nameTv = (TextView) convertView.findViewById(R.id.contectName);
        final CheckBox cb     = (CheckBox) convertView.findViewById(R.id.contactCheckbox);

        nameTv.setText(contactListItems.get(position).getName());
        cb.setChecked(contactListItems.get(position).getSelected());

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
