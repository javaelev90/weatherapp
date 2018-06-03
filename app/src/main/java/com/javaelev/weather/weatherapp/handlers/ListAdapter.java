package com.javaelev.weather.weatherapp.handlers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.javaelev.weather.weatherapp.R;
import com.javaelev.weather.weatherapp.model.ForecastItem;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListAdapter extends ArrayAdapter<ForecastItem>{

    private List<ForecastItem> forecastItems;
    private Context context;

    private static class ListItemViewHolder{
        ImageView symbol;
        TextView degrees;
        TextView dateTime;
        TextView windSpeed;
        TextView precipitation;
    }

    public ListAdapter(@NonNull Context context, int resource, @NonNull List<ForecastItem> objects) {
        super(context, resource, objects);
        this.context = context;
        forecastItems = objects;

    }

    public List<ForecastItem> getForecastItems(){
        return forecastItems;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        ForecastItem forecastItem = forecastItems.get(position);
        final ListItemViewHolder listItemViewHolder;
        if(convertView == null){
            listItemViewHolder = new ListItemViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_row_layout, parent, false);

            listItemViewHolder.symbol = convertView.findViewById(R.id.list_item_weatherSymbol);
            listItemViewHolder.degrees = convertView.findViewById(R.id.list_item_degrees);
            listItemViewHolder.dateTime = convertView.findViewById(R.id.list_item_time);
            listItemViewHolder.windSpeed = convertView.findViewById(R.id.list_item_wind);
            listItemViewHolder.precipitation = convertView.findViewById(R.id.list_item_precipitation);
            convertView.setTag(listItemViewHolder);
        } else {
            listItemViewHolder = (ListItemViewHolder) convertView.getTag();
        }
        //String url for a weather symbol from YR:s API ex. A sun behind a cloud
        String symbolUrl = context.getText(R.string.symbolURL)+forecastItem.getSymbolCode()+".png";
        Picasso.get().load(symbolUrl).into(listItemViewHolder.symbol);
        listItemViewHolder.windSpeed.setText(forecastItem.getWindSpeed()+"mps "+forecastItem.getWindDirection());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM 'at:' HH:mm");
        listItemViewHolder.dateTime.setText(forecastItem.getForecastTime().format(formatter).toString());
        listItemViewHolder.degrees.setText((int)forecastItem.getDegreesCelsius()+"\u00b0 C");
        if(forecastItem.getPrecipitationMin() == forecastItem.getPrecipitationMax()){
            listItemViewHolder.precipitation.setText(forecastItem.getPrecipitationMin()+" mm rain");
        } else {
            listItemViewHolder.precipitation.setText(forecastItem.getPrecipitationMin()+" - "+forecastItem.getPrecipitationMax()+" mm rain");
        }

        return convertView;
    }
}
