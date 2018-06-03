package com.javaelev.weather.weatherapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.javaelev.weather.weatherapp.handlers.FileLoader;
import com.javaelev.weather.weatherapp.handlers.ForecastCallback;
import com.javaelev.weather.weatherapp.handlers.ListAdapter;
import com.javaelev.weather.weatherapp.handlers.YrFileLoader;
import com.javaelev.weather.weatherapp.model.CityInfo;
import com.javaelev.weather.weatherapp.model.ForecastItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {

//    private FileLoader fileLoader;
    private YrFileLoader yrFileLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "https://www.yr.no/place/Sweden/Norrbotten/Lule%C3%A5/forecast.xml";
        loadForecast(url, "Luleå");
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setLogo(R.drawable.ic_main_activity_icon);
        setSupportActionBar(myToolbar);

        ConstraintLayout layout = findViewById(R.id.container);
        layout.setOnClickListener((v) -> hideKeyboard());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        AutoCompleteTextView searchView = findViewById(R.id.searchView);
        searchView.setAdapter(adapter);

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                List<String> cities = (List<String>)msg.obj;
                adapter.addAll(cities);
                adapter.notifyDataSetChanged();

            }
        };
//        fileLoader = new FileLoader(handler, getResources());
//        fileLoader.start();
        yrFileLoader = new YrFileLoader(handler, getResources());
        yrFileLoader.start();

        searchView.setOnItemClickListener((parent, view, position, id) -> {
            String cityName = (String)parent.getAdapter().getItem(position);
//            loadForecast(buildUrl(cityName), cityName);
            loadForecast(yrFileLoader.getForecastURL(cityName), cityName);
            searchView.dismissDropDown();
            searchView.setText("");
            hideKeyboard();
            view.clearFocus();
        });
        searchView.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH && !searchView.getAdapter().isEmpty()){
                String searchViewText = searchView.getText().toString().trim();
                if(!(searchViewText.equals("") || searchView.getDropDownHeight() == 0)){

                    String cityName = (String) searchView.getAdapter().getItem(0);
                    System.out.println(cityName);
//                    loadForecast(buildUrl(cityName), cityName);
                    loadForecast(yrFileLoader.getForecastURL(cityName), cityName);
                    searchView.dismissDropDown();
                    searchView.setText("");
                }
                v.clearFocus();
                hideKeyboard();
                return true;
            }
            return false;
        });
    }

    private void hideKeyboard(){

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void loadForecast(String url, String cityName){
        ListView listView = findViewById(R.id.forecastList);
        listView.setClickable(true);
        listView.setOnItemClickListener((parent, view, position, id) -> hideKeyboard());

        ListAdapter adapter = new ListAdapter(getApplicationContext(), R.layout.list_row_layout, new ArrayList<>());
        listView.setAdapter(adapter);

        //This handler is needed to let UI thread re-draw list view after async YR-api call has returned
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                List<ForecastItem> forecast = (List<ForecastItem>)msg.obj;
                TextView city = findViewById(R.id.header_city);
                TextView time = findViewById(R.id.header_time);
                TextView precipitation = findViewById(R.id.header_rain_info);
                TextView degrees = findViewById(R.id.header_degrees);
                TextView wind = findViewById(R.id.header_wind_info);
                ImageView symbol = findViewById(R.id.header_symbol);
                ForecastItem firstItem = forecast.get(0);
                city.setText(cityName);
                time.setText("Today at: "+firstItem.getForecastTime().toLocalTime().toString());
                degrees.setText((int)firstItem.getDegreesCelsius()+"\u00b0 C");
                wind.setText(firstItem.getWindSpeed()+"mps "+firstItem.getWindDirection());
                if(firstItem.getPrecipitationMin() == firstItem.getPrecipitationMax()){
                    precipitation.setText(firstItem.getPrecipitationMin()+" mm rain");
                }  else {
                    precipitation.setText(firstItem.getPrecipitationMin()+" - "+firstItem.getPrecipitationMax()+" mm");
                }
                String symbolUrl = "https://raw.githubusercontent.com/YR/weather-symbols/master/dist/png/100/"+firstItem.getSymbolCode()+".png";
//                String symbolUrl = "http://symbol.yr.no/grafikk/sym/b200/"+firstItem.getSymbolCode()+".png";
                Picasso.get().load(symbolUrl).into(symbol);
                forecast.remove(0);
                adapter.addAll(forecast);
                adapter.notifyDataSetChanged();
            }
        };


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        if(adapter.getCount() == 0){
            client.newCall(request).enqueue(new ForecastCallback(handler));
        }
    }

//    /**
//     * Builds a url for YRs weather API
//     * URL format: "https://www.yr.no/place/<Country>/<CountryArea>/<City>/forecast.xml"
//     * @param cityName name of city
//     * @return a url following the format
//     */
//    private String buildUrl(String cityName){
//        //Holds city name, city area code and country name
//        CityInfo cityInfo = fileLoader.getCityInfo(cityName);
//        //Country code e.g. SE for Sweden
//        String countryName = fileLoader.getCountryName(cityInfo.getCountryCode());
//        //This has to be a concatenation between countryCode + "." + countryAreaCode. Example SE.14 i.e., sweden.norrbotten
//        String countryArea = fileLoader.getCountryArea(cityInfo.getCountryCode()+"."+cityInfo.getCityAreaCode());
//
//        // %20 means blank space in url language, can't have regular spaces in an url
//        countryName = countryName.replaceAll(" ", "%20");
//        countryArea = countryArea.replaceAll(" ", "%20");
//        cityName = cityName.replaceAll(" ", "%20");
//
//        // Example url: "https://www.yr.no/place/Sweden/Norrbotten/Luleå/forecast.xml";
//        String url = "https://www.yr.no/place/"+countryName+"/"+countryArea+"/"+cityName+"/forecast.xml";
//        Log.i("GET forecast", url);
//        return url;
//    }

}
