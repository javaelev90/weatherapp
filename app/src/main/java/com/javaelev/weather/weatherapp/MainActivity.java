package com.javaelev.weather.weatherapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.javaelev.weather.weatherapp.handlers.FileLoader;
import com.javaelev.weather.weatherapp.handlers.ForecastCallback;
import com.javaelev.weather.weatherapp.handlers.ListAdapter;
import com.javaelev.weather.weatherapp.handlers.YrFileLoader;
import com.javaelev.weather.weatherapp.model.CityInfo;
import com.javaelev.weather.weatherapp.model.ForecastItem;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;


public class MainActivity extends AppCompatActivity {

    private FileLoader fileLoader;
    private YrFileLoader yrFileLoader;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);
        String url = "https://api.met.no/weatherapi/locationforecast/2.0/classic?lat=60.10&lon=9.58";
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
        fileLoader = new FileLoader(handler, getResources());
        fileLoader.start();
//        yrFileLoader = new YrFileLoader(handler, getResources());
//        yrFileLoader.start();

        searchView.setOnItemClickListener((parent, view, position, id) -> {
            String cityName = (String)parent.getAdapter().getItem(position);
            loadForecast(buildUrl(cityName), cityName);
//            loadForecast(yrFileLoader.getForecastURL(cityName), cityName);
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
                    loadForecast(buildUrl(cityName), cityName);
//                    loadForecast(yrFileLoader.getForecastURL(cityName), cityName);
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

    private void loadForecast(String url, String cityName) {
        ListView listView = findViewById(R.id.forecastList);
        listView.setClickable(true);
        listView.setOnItemClickListener((parent, view, position, id) -> hideKeyboard());

        ListAdapter adapter = new ListAdapter(getApplicationContext(), R.layout.list_row_layout, new ArrayList<>());
        listView.setAdapter(adapter);

        //This handler is needed to let UI thread re-draw list view after async YR-api call has returned
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(msg.obj == null){
                    return;
                }
                List<ForecastItem> forecast = (List<ForecastItem>)msg.obj;
                TextView city = findViewById(R.id.header_city);
                TextView time = findViewById(R.id.header_time);
                TextView precipitation = findViewById(R.id.header_rain_info);
                TextView degrees = findViewById(R.id.header_degrees);
                TextView wind = findViewById(R.id.header_wind_info);
                ImageView symbol = findViewById(R.id.header_symbol);
                ForecastItem firstItem = forecast.get(0);
                city.setText(cityName);
                time.setText("Today at: "+firstItem.getForecastTimeFrom().toLocalTime().toString());
                degrees.setText((int)firstItem.getDegreesCelsius()+"\u00b0 C");
                wind.setText(firstItem.getWindSpeed()+"mps "+firstItem.getWindDirection());
                if(firstItem.getPrecipitationMin() == firstItem.getPrecipitationMax()){
                    precipitation.setText(firstItem.getPrecipitationMin()+" mm rain");
                }  else {
                    precipitation.setText(firstItem.getPrecipitationMin()+" - "+firstItem.getPrecipitationMax()+" mm");
                }
//                String symbolUrl = "https://raw.githubusercontent.com/YR/weather-symbols/master/dist/png/100/"+firstItem.getSymbolCode()+".png";
////                String symbolUrl = "http://symbol.yr.no/grafikk/sym/b200/"+firstItem.getSymbolCode()+".png";
//                Picasso.get().load(symbolUrl).into(symbol);
                forecast.remove(0);
                adapter.addAll(forecast);
                adapter.notifyDataSetChanged();
            }
        };


        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .header("User-Agent", "acmeweathersite.com support@acmeweathersite.com")
                .build();

        if(adapter.getCount() == 0){
            client.newCall(request).enqueue(new ForecastCallback(handler));
        }

    }

    /**
     * Builds a url for YRs weather API
     * URL format: "https://www.yr.no/place/<Country>/<CountryArea>/<City>/forecast.xml"
     * @param cityName name of city
     * @return a url following the format
     */
    private String buildUrl(String cityName){
        //Holds city name, city area code and country name
        CityInfo cityInfo = fileLoader.getCityInfo(cityName);

        // Example url: "https://api.met.no/weatherapi/locationforecast/2.0/classic?lat=60.10&lon=9.58";
        String url = "https://api.met.no/weatherapi/locationforecast/2.0/classic?lat="+cityInfo.getLat()+"&lon="+cityInfo.getLon();
        Log.i("GET forecast", url);
        return url;
    }

}
