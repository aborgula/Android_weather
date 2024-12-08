package com.example.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

// import com.example.weather.R;

public class MainActivity extends AppCompatActivity {

    Context context;

    EditText editText;
    Button button;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    private String text;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        if(!isConnected()){
            Toast.makeText(MainActivity.this,"No Internet Acess", Toast.LENGTH_SHORT).show();
            button.setVisibility(View.GONE);
        }
        loadData();
        updateViews();

    }

       public void buttonSenderPressed(View v) {
           saveData();
           String city = editText.getText().toString();

           if (!TextUtils.isEmpty(city) && isValidCityName(city)) {
               Intent intent = new Intent(this, WeatherActivity.class);
               intent.putExtra("KEY_SENDER", editText.getText().toString());
               startActivity(intent);
           }

       }

       public boolean isValidCityName(String city){
        return city.matches("[a-zA-Z ]+");
       }

       public void saveData(){
           editText = findViewById(R.id.textView);
           String tekst = editText.getText().toString();
           SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
           SharedPreferences.Editor editor = sharedPreferences.edit();
           editor.putString(TEXT, tekst);

           editor.apply();

           Toast.makeText(this, "Data saves", Toast.LENGTH_LONG).show();
       }

       public void loadData(){
           SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
           text = sharedPreferences.getString(TEXT, "");
       }

       public void updateViews(){
           editText.setText(text);
       }

    private boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}