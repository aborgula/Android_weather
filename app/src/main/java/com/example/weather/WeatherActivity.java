package com.example.weather;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;

    TextView textView2;
    TextView temp;
    TextView pressure;
    ImageView imageView;
    TextView godzina;
    TextView humidity;
    TextView tempmin;
    TextView tempmax;
    private ImageView weatherIcon;
    private final String url = "http://api.openweathermap.org/data/2.5/weather";
    private final String appid = "749561a315b14523a8f5f1ef95e45864";
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather2);
        imageView = findViewById(R.id.internet);



        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWeatherData();
            }
        });

        weatherIcon = findViewById(R.id.weatherIcon);
        textView2 = findViewById(R.id.textView2);
        temp = findViewById(R.id.temp);
        tempmin = findViewById(R.id.tempmin);
        tempmax = findViewById(R.id.tempmax);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);

        Intent receiver = getIntent();
        String cityName = receiver.getStringExtra("KEY_SENDER");
        textView2.setText(cityName);

        getWeatherData(cityName);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshWeatherData();
            }
        }, 0, 300000);

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(!isConnected()){
                                    Toast.makeText(WeatherActivity.this,"No Internet Acess", Toast.LENGTH_SHORT).show();
                                    imageView.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setEnabled(false);

                                }

                                godzina = findViewById(R.id.godzina);
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                String currentTime = sdf.format(new Date());
                                godzina.setText(currentTime);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }

    private void refreshWeatherData() {
        String city = textView2.getText().toString().trim();
        getWeatherData(city);
    }

    private void getWeatherData(String city) {
        String tempurl = url + "?q=" + city + ",pl&appid=" + appid + "&units=metric";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getWeatherDetails(response);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void getWeatherDetails(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);

            JSONArray jsonArrayWeather = jsonResponse.getJSONArray("weather");
            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
            String iconCode = jsonObjectWeather.getString("icon");
            String iconUrl = "https://openweathermap.org/img/w/" + iconCode + ".png";

            ImageView weatherIcon = findViewById(R.id.weatherIcon);
            Picasso.get().load(iconUrl).into(weatherIcon);

            JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
            double tempValue = jsonObjectMain.getDouble("temp");
            float pressureValue = jsonObjectMain.getInt("pressure");
            double tempMinValue = jsonObjectMain.getDouble("temp_min");
            double tempMaxValue = jsonObjectMain.getDouble("temp_max");
            int humidityValue = jsonObjectMain.getInt("humidity");

            temp.setText(String.format("%.2f °C", tempValue));
            pressure.setText(String.format("%.2f hPa", pressureValue));
            tempmin.setText(String.format("%.2f °C", tempMinValue));
            tempmax.setText(String.format("%.2f °C", tempMaxValue));
            humidity.setText(String.format("%d%%", humidityValue));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Context context;
   private boolean isConnected(){
       ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
   }
}
