package com.javaelev.weather.weatherapp.handlers;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.javaelev.weather.weatherapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YrFileLoader extends Thread{

    private HashMap<String, String> cityToAPIURL;
    private Resources resources;
    private Handler handler;

    public YrFileLoader(Handler handler, Resources resources){
        this.resources = resources;
        this.handler = handler;
    }

    @Override
    public void run(){
        cityToAPIURL = new HashMap<>();
        loadCityToAPIURLMapping(R.raw.verda, 3, 9);
        loadCityToAPIURLMapping(R.raw.noreg, 1, 9);
        List<String> cities = new ArrayList<>(cityToAPIURL.keySet());
        Message message = handler.obtainMessage(1, cities);
        message.sendToTarget();
    }
    public String getForecastURL(String cityName){
        return cityToAPIURL.get(cityName);
    }

    private HashMap<String, String> loadCityToAPIURLMapping(int resourceId, int keyIndex, int valueIndex){


        InputStream inputStream = resources.openRawResource(resourceId);
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){
            //First line is just a description i.e. Name,Code
            reader.readLine();

            //First actual (city, forecast url) line
            String line = reader.readLine();
            while(line != null){
                String[] cityAndCountryCode = line.split("\t");
                //String[] countryAndCode = {line.substring(0, index), line.substring(index+1)};

//                System.out.format("Country: %-50s Code: %-2s%n",countryAndCode[0] , countryAndCode[1]);
                cityToAPIURL.put(cityAndCountryCode[keyIndex],cityAndCountryCode[cityAndCountryCode.length-1]);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Loading done","Made city to forecast url mapping.");
        return cityToAPIURL;
    }



}
