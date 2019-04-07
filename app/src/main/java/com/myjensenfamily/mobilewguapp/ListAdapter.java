/**
 * ListAdapter works with the ListItem interface in order to allow one view to be reused for different lists as long as they implement
 *          the ListItem interface
 * By: George W. Jensen III
 * Last Update 04/07/19
 */


package com.myjensenfamily.mobilewguapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter<T> extends ArrayAdapter<T> implements Contains{

    private List<T> listItems;
    private List<T> listSelectedItems;
    private List<T> listExcludedItems;
    private List<View> listSelectedRows;

    public ListAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
         listItems = objects;
         listSelectedItems = new ArrayList<>();
         listSelectedRows = new ArrayList<>();
         listExcludedItems = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.list_item, parent, false);
        }

        T item = listItems.get(position);

        TextView nameText = (TextView) convertView.findViewById(R.id.nameText);
        nameText.setText(""+((ListItem) item).getName());

        TextView infoText = (TextView) convertView.findViewById(R.id.infoText);
        infoText.setText(""+((ListItem) item).getInfo());

        return convertView;
    }

    public void handleLongPress(int position, View view){
        if(listSelectedRows.contains(view)){
            listSelectedRows.remove(view);
            listSelectedItems.remove(listItems.get(position));
            view.setBackgroundResource(R.color.colorOffWhite);
        } else
        {
            listSelectedItems.add(listItems.get(position));
            listSelectedRows.add(view);
            view.setBackgroundResource(R.color.colorDarkGrey);
        }
    }


    public List<T> deleteSelectedItems(){

        for(T item : listSelectedItems)
        {
            if(((Contains)item).isEmpty())
            {
                listExcludedItems.add(item);
            }
        }

        if(listExcludedItems.isEmpty())
        {

                Toast.makeText(this.getContext(), "Can not delete items that are not empty", Toast.LENGTH_LONG).show();
                listSelectedItems.removeAll(listExcludedItems);
        }
        else {
            listItems.removeAll(listSelectedItems);
        }
            listExcludedItems.clear();

        for(View view : listSelectedRows)
        {
            view.setBackgroundResource(R.color.colorWhite);
        }
        listSelectedRows.clear();
        return listSelectedItems;
    }

    public List<T> getListItems() {
        return listItems;
    }

    public void setListItems(List<T> listItems) {
        this.listItems = listItems;
    }

    public List<T> getListSelectedItems() {
        return listSelectedItems;
    }

    public void setListSelectedItems(List<T> listSelectedItems) {
        this.listSelectedItems = listSelectedItems;
    }

    public List<View> getListSelectedRows() {
        return listSelectedRows;
    }

    public void setListSelectedRows(List<View> listSelectedRows) {
        this.listSelectedRows = listSelectedRows;
    }
}
