package com.javaelev.weather.weatherapp.handlers;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import com.javaelev.weather.weatherapp.R;
import com.javaelev.weather.weatherapp.model.ForecastItem;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ForecastCallback implements Callback {

    private Handler handler;
    private Activity mainActivity;

    public ForecastCallback(Handler handler){
        this.handler = handler;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        e.printStackTrace();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if(!response.isSuccessful()){
            Log.e("Forecast", "Could not get forecast");
        }
        ForecastXMLParser parser = new ForecastXMLParser();
        List<ForecastItem> forecast = parser.getForecast(response.body().string());
        Log.i("FORECAST", forecast.toString());
        Message forecastMsg = handler.obtainMessage(1,forecast);
        forecastMsg.sendToTarget();


    }
}
