package com.javaelev.weather.weatherapp.handlers;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.javaelev.weather.weatherapp.model.ForecastItem;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ForecastCallback implements Callback {

    private Handler handler;

    public ForecastCallback(Handler handler){
        this.handler = handler;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        e.printStackTrace();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if(!response.isSuccessful()){
            Log.e("Forecast", "Could not get forecast");
        } else {
            ForecastXMLParser parser = new ForecastXMLParser();
            List<ForecastItem> forecast = parser.getForecast(response.body().string());
            Message forecastMsg = handler.obtainMessage(1,forecast);
            forecastMsg.sendToTarget();
        }
    }
}
