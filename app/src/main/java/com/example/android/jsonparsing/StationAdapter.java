package com.example.android.jsonparsing;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tommi on 24.9.2016.
 */
public class StationAdapter extends BaseAdapter {

    Context context;
    ArrayList<Station> listData;

    public StationAdapter(Context context,ArrayList<Station> listData){
        this.context = context;
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    class ViewHolder {
        private TextView textViewStationName;
    }
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.station_item,null);
            viewHolder = new ViewHolder();
            viewHolder.textViewStationName = (TextView) view.findViewById(R.id.txtViewStationName);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        Station station = listData.get(position);
        String stationName = station.getStationName();
        viewHolder.textViewStationName.setText(stationName);
        return view;
    }
}
