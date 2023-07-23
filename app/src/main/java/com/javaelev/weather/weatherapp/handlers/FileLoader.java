package com.javaelev.weather.weatherapp.handlers;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;

import com.javaelev.weather.weatherapp.R;
import com.javaelev.weather.weatherapp.model.CityInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class FileLoader extends Thread{

    private Handler handler;
    private Resources resources;
    private HashMap<String, String> codeToCountryMapping;
    private HashMap<String, CityInfo> cityToCityInfoMapping;
    private HashMap<String, String> countryAreaCodeToCountryAreaMapping;

    public FileLoader(Handler handler, Resources resources){
        this.handler = handler;
        this.resources = resources;
        codeToCountryMapping = new HashMap<>();
        cityToCityInfoMapping = new HashMap<>();
        countryAreaCodeToCountryAreaMapping = new HashMap<>();
    }

    @Override
    public void run(){
        map((lineInput) -> {
            String[] cityAndCountryCode = lineInput.split("\t");
            cityToCityInfoMapping.put(cityAndCountryCode[1], new CityInfo(cityAndCountryCode[1],
                    cityAndCountryCode[10],cityAndCountryCode[8], Float.parseFloat(cityAndCountryCode[4]),
                    Float.parseFloat(cityAndCountryCode[5])));
        },
        R.raw.cities1000);
        map((lineString) -> {
            String[] cityAndCountryCode = lineString.split("\t");
            countryAreaCodeToCountryAreaMapping.put(cityAndCountryCode[0],cityAndCountryCode[1]);
        },
        R.raw.admin1codesascii);
        map((lineString) -> {
            int index = lineString.lastIndexOf(",");
            codeToCountryMapping.put(lineString.substring(index+1),lineString.substring(0, index));
        },
        R.raw.data_csv);
        List<String> cities = new ArrayList<>(cityToCityInfoMapping.keySet());
        Message message = handler.obtainMessage(1, cities);
        message.sendToTarget();
    }

    public String getCountryName(String countryCode){
        return codeToCountryMapping.get(countryCode);
    }

    public CityInfo getCityInfo(String city){
        return cityToCityInfoMapping.get(city);
    }

    public String getCountryArea(String areaCode){
        return countryAreaCodeToCountryAreaMapping.get(areaCode);
    }

    private void map(Consumer<String> mapper, int resourceId){
        try(InputStream inputStream = resources.openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){
            //First line is just a description i.e. Name,Code
            reader.readLine();

            //First actual (country, country code) line
            String line = reader.readLine();
            while(line != null){
                mapper.accept(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
