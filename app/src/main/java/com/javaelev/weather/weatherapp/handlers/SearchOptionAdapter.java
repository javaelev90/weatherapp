package com.javaelev.weather.weatherapp.handlers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.List;

public class SearchOptionAdapter extends ArrayAdapter<String> {
    public SearchOptionAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);

    }




}
