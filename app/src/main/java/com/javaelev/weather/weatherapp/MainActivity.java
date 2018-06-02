package com.javaelev.weather.weatherapp;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.javaelev.weather.weatherapp.handlers.FileLoader;
import com.javaelev.weather.weatherapp.handlers.ForecastCallback;
import com.javaelev.weather.weatherapp.handlers.ListAdapter;
import com.javaelev.weather.weatherapp.model.ForecastItem;
import com.squareup.picasso.Picasso;

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

                    //This handler is needed to let UI thread re-draw list view after async YR-api call has returned
                    Handler handler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            List<ForecastItem> forecast = (List<ForecastItem>)msg.obj;
                            TextView time = findViewById(R.id.header_time);
                            TextView degrees = findViewById(R.id.header_degrees);
                            TextView desc = findViewById(R.id.header_symbol_description);
                            ImageView symbol = findViewById(R.id.header_symbol);
                            ForecastItem firstItem = forecast.get(0);

                            time.setText("Today at: "+firstItem.getForecastTime().toLocalTime().toString());
                            degrees.setText((int)firstItem.getDegreesCelsius()+"\u00b0 C");
                            desc.setText(firstItem.getSymbolMeaning());
                            String symbolUrl = "http://symbol.yr.no/grafikk/sym/b200/"+firstItem.getSymbolCode()+".png";
                            Picasso.get().load(symbolUrl).into(symbol);
                            forecast.remove(0);
                            adapter.addAll(forecast);
                            adapter.notifyDataSetChanged();
                        }
                    };

                    String url = "https://www.yr.no/place/Sweden/Norrbotten/Lule%C3%A5/forecast.xml";
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).build();
                    if(adapter.getCount() == 0){
                        client.newCall(request).enqueue(new ForecastCallback(handler));
                    }

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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
                final String[] COUNTRIES = new String[] { "Belgium",
                "France", "France_", "Italy", "Germany", "Spain" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.editText1);
        textView.setAdapter(adapter);
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowCustomEnabled(true);
//        // actionBar.setDisplayShowTitleEnabled(false);
//        // actionBar.setIcon(R.drawable.ic_action_search);
//
//        LayoutInflater inflator = (LayoutInflater) this
//                .getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
//        View v = inflator.inflate(R.layout.actionbar, null);
//
//        actionBar.setCustomView(v);
//        final String[] COUNTRIES = new String[] { "Belgium",
//                "France", "France_", "Italy", "Germany", "Spain" };
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
//        AutoCompleteTextView textView = (AutoCompleteTextView) v
//                .findViewById(R.id.editText1);
//        textView.setAdapter(adapter);

//        FileLoader fileLoader = new FileLoader();
//        fileLoader.makeCountryCodesHashMap(getResources());

    }

}
