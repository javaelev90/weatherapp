package com.javaelev.weather.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import com.javaelev.weather.weatherapp.handlers.FileLoader;
import com.javaelev.weather.weatherapp.handlers.ForecastCallback;
import com.javaelev.weather.weatherapp.handlers.ListAdapter;
import com.javaelev.weather.weatherapp.model.CityInfo;
import com.javaelev.weather.weatherapp.model.ForecastItem;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;


public class MainActivity extends AppCompatActivity {

    private FileLoader fileLoader;
    private Request request;
    private OkHttpClient client;
    private ListAdapter foreCastListAdapter;
    private Handler handleForeCast;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        client = new OkHttpClient();

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setLogo(R.drawable.ic_main_activity_icon);
        setSupportActionBar(myToolbar);

        ConstraintLayout layout = findViewById(R.id.container);
        layout.setOnClickListener((v) -> hideKeyboard());

        ListView listView = findViewById(R.id.forecastList);
        listView.setClickable(true);
        listView.setOnItemClickListener((parent, view, position, id) -> hideKeyboard());
        foreCastListAdapter = new ListAdapter(getApplicationContext(), R.layout.list_row_layout, new ArrayList<>());
        listView.setAdapter(foreCastListAdapter);

        //This handler is needed to let UI thread re-draw list view after async YR-api call has returned
        handleForeCast = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(msg.obj == null) return;

                List<ForecastItem> forecast = (List<ForecastItem>)msg.obj;

                TextView time = findViewById(R.id.header_time);
                TextView precipitation = findViewById(R.id.header_rain_info);
                TextView degrees = findViewById(R.id.header_degrees);
                TextView wind = findViewById(R.id.header_wind_info);
                ImageView symbol = findViewById(R.id.header_symbol);

                ForecastItem firstItem = forecast.get(0);

                time.setText("Today at: "+firstItem.getForecastTimeFrom().toLocalTime().toString());
                degrees.setText((int)firstItem.getDegreesCelsius()+"\u00b0 C");
                wind.setText(firstItem.getWindSpeed()+"mps "+firstItem.getWindDirection());
                if(firstItem.getPrecipitationMin() == firstItem.getPrecipitationMax()){
                    precipitation.setText(firstItem.getPrecipitationMin()+" mm rain");
                }  else {
                    precipitation.setText(firstItem.getPrecipitationMin()+" - "+firstItem.getPrecipitationMax()+" mm");
                }

                int symbolResource = getResources().getIdentifier(firstItem.getSymbolCode(), "drawable", getPackageName());
                symbol.setImageResource(symbolResource);
                forecast.remove(0);
                foreCastListAdapter.clear();
                foreCastListAdapter.addAll(forecast);
                foreCastListAdapter.notifyDataSetChanged();
            }
        };

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
                loadStarredForecast();
            }
        };
        fileLoader = new FileLoader(handler, getResources());
        fileLoader.start();

        searchView.setOnItemClickListener((parent, view, position, id) -> {
            String cityName = (String)parent.getAdapter().getItem(position);
            triggerSearch(cityName, searchView);
            hideKeyboard();
            view.clearFocus();
        });
        searchView.setOnEditorActionListener((view, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH && !searchView.getAdapter().isEmpty()){
                String searchViewText = searchView.getText().toString().trim();
                if(!(searchViewText.equals("") || searchView.getDropDownHeight() == 0)){
                    String cityName = (String) searchView.getAdapter().getItem(0);
                    triggerSearch(cityName, searchView);
                }
                view.clearFocus();
                hideKeyboard();
                return true;
            }
            return false;
        });

    }

    private void loadStarredForecast(){
        String cityName = sharedPreferences.getString("starred-city", "");
        cityName = cityName.equals("") ? "Stockholm" : cityName;
        sharedPreferences.edit().putString("starred-city", cityName).commit();
        TextView city = findViewById(R.id.header_city);
        city.setText(cityName);
        ImageButton imgButton = findViewById(R.id.starredCityBtn);
        imgButton.setImageResource(android.R.drawable.btn_star_big_on);
        loadForecast(buildUrl(cityName));
        setupButtons();
    }

    private void setupButtons(){
        final ImageButton imgButton = findViewById(R.id.starredCityBtn);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentCity = sharedPreferences.getString("current-city", "Stockholm");
                String starredCity = sharedPreferences.getString("starred-city", "Stockholm");

                if (currentCity.equals(starredCity)) {
                    imgButton.setImageResource(android.R.drawable.btn_star_big_off);
                } else {
                    imgButton.setImageResource(android.R.drawable.btn_star_big_on);
                    sharedPreferences.edit().putString("starred-city", currentCity).commit();
                }
            }
        });
    }

    private void triggerSearch(String cityName, AutoCompleteTextView searchView){
        final ImageButton imgButton = findViewById(R.id.starredCityBtn);
        final TextView city = findViewById(R.id.header_city);
        city.setText(cityName);
        loadForecast(buildUrl(cityName));

        sharedPreferences.edit().putString("current-city", cityName).commit();
        String starredCityName = sharedPreferences.getString("starred-city", "Stockholm");
        if(cityName.equals(starredCityName)){
            imgButton.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            imgButton.setImageResource(android.R.drawable.btn_star_big_off);
        }
        searchView.dismissDropDown();
        searchView.setText("");
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void loadForecast(String url) {
        request = new okhttp3.Request.Builder()
                .url(url)
                .header("User-Agent", "acmeweathersite.com support@acmeweathersite.com")
                .build();
        client.newCall(request).enqueue(new ForecastCallback(handleForeCast));
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
