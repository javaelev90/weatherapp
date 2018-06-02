package com.javaelev.weather.weatherapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.javaelev.weather.weatherapp.handlers.FileLoader;
import com.javaelev.weather.weatherapp.handlers.ForecastCallback;
import com.javaelev.weather.weatherapp.handlers.ListAdapter;
import com.javaelev.weather.weatherapp.model.ForecastItem;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {



        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);

                    ListView listView = findViewById(R.id.forecastList);
                    ListAdapter adapter = new ListAdapter(getApplicationContext(), R.layout.list_view_row, new ArrayList<ForecastItem>());
                    listView.setAdapter(adapter);
                    String url = "https://www.yr.no/place/Sweden/Norrbotten/Lule%C3%A5/forecast.xml";
                    //                    ForecastXMLParser forecastRetriever = new ForecastXMLParser();
//                    forecastRetriever.getForecast(url);
//                    LocalDateTime forecastTime, String windDirection, float degreesCelsius, float windSpeed, String symbolCode, String symbolMeaning
                    OkHttpClient client = new OkHttpClient();
//                    ForecastCallback forecastCallback = new ForecastCallback(adapter);

                    Request request = new Request.Builder().url(url).build();

                    Handler handler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            List<ForecastItem> forecast = (List<ForecastItem>)msg.obj;
                            TextView time = findViewById(R.id.header_time);
                            TextView degrees = findViewById(R.id.header_degrees);
                            TextView desc = findViewById(R.id.header_symbol_description);
                            ImageView symbol = findViewById(R.id.header_symbol);
                            ForecastItem firstItem = forecast.get(0);
                            time.setText(firstItem.getForecastTime().toString());
                            degrees.setText(firstItem.getDegreesCelsius()+"\u00b0 C");
                            desc.setText(firstItem.getSymbolMeaning());
                            String symbolUrl = "http://symbol.yr.no/grafikk/sym/b200/"+firstItem.getSymbolCode()+".png";
                            Picasso.get().load(symbolUrl).into(symbol);
                            forecast.remove(0);
                            adapter.addAll(forecast);
                            adapter.notifyDataSetChanged();
                        }
                    };
                    if(adapter.getCount() == 0){
                        client.newCall(request).enqueue(new ForecastCallback(handler));
                    }









//                    List<ForecastItem> forecast = new ArrayList<>();
//                    ForecastItem forecastItem = new ForecastItem(LocalDateTime.now(), "North West", 23, 1.1f, "02d", "Cloudy");
//                    forecast.add(forecastItem);
//                    forecast.add(forecastItem);
//                    forecast.add(forecastItem);
//                    forecast.add(forecastItem);
//                    forecast.add(forecastItem);
//                    forecast.add(forecastItem);
//                    forecast.add(forecastItem);
//                    forecast.add(forecastItem);
//                    forecast.add(forecastItem);
//
//                    listView.setAdapter(new ListAdapter(getApplicationContext(), R.layout.list_view_row, forecast));
                    return true;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_dashboard);

                    return true;
                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);
//        FileLoader fileLoader = new FileLoader();
//        fileLoader.makeCountryCodesHashMap(getResources());

    }

}
