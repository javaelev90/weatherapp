package com.javaelev.weather.weatherapp.handlers;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.javaelev.weather.weatherapp.R;
import com.javaelev.weather.weatherapp.model.ForecastItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ListAdapter extends ArrayAdapter<ForecastItem>{

    private List<ForecastItem> forecastItems;
    private Context context;

    private static class ListItemViewHolder{
        ImageView symbol;
        TextView degrees;
        TextView dateTime;
        TextView symbolMeaning;
    }

    public ListAdapter(@NonNull Context context, int resource, @NonNull List<ForecastItem> objects) {
        super(context, resource, objects);
        this.context = context;
        forecastItems = objects;

    }

    public View getView(int position, View convertView, ViewGroup parent){

        ForecastItem forecastItem = forecastItems.get(position);
        final ListItemViewHolder listItemViewHolder;
        if(convertView == null){
            listItemViewHolder = new ListItemViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_view_row, parent, false);

            listItemViewHolder.symbol = convertView.findViewById(R.id.list_item_weatherSymbol);
            listItemViewHolder.degrees = convertView.findViewById(R.id.list_item_degrees);
            listItemViewHolder.dateTime = convertView.findViewById(R.id.list_item_time);
            listItemViewHolder.symbolMeaning = convertView.findViewById(R.id.list_item_symbolDescription);
            convertView.setTag(listItemViewHolder);
        } else {
            listItemViewHolder = (ListItemViewHolder) convertView.getTag();
        }
        //String url for a weather symbol from YR:s API ex. A sun behind a cloud
        String symbolUrl = context.getText(R.string.symbolURL)+forecastItem.getSymbolCode()+".png";
        Picasso.get().load(symbolUrl).into(listItemViewHolder.symbol);
        listItemViewHolder.symbolMeaning.setText(forecastItem.getSymbolMeaning());
        listItemViewHolder.dateTime.setText(forecastItem.getForecastTime().toString());
        listItemViewHolder.degrees.setText(forecastItem.getDegreesCelsius()+"\u00b0 C");

        return convertView;
    }

//    public void addAll(ForecastItem... forecastItems){
//        IntStream.range(0, forecastItems.length).forEach(i -> this.forecastItems.add(forecastItems[i]));
//        notifyDataSetChanged();
//
//    }
//
//    public int getCount(){
//        return forecastItems.size();
//    }
}
