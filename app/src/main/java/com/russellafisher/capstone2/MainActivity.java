package com.russellafisher.capstone2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;
import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.EditText;



public class MainActivity extends AppCompatActivity {

    private Button GPSBtn;
    private TextView GPSResults;
    private TextView Value;
    private LocationManager locationManager;
    private LocationListener listener;
    public Double latitude;
    public Double longitude;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GPSResults = (TextView) findViewById(R.id.GPS_SearchResults);
        Value = (TextView) findViewById(R.id.Value);
        GPSBtn = (Button) findViewById(R.id.GPS_Search);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        requestQueue = Volley.newRequestQueue(this);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                JsonObjectRequest request = new JsonObjectRequest("https://fathomless-thicket-83996.herokuapp.com/geocode/json?lat="+latitude+"&lng="+longitude, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String address = response.getJSONObject("SearchResults:searchresults").getJSONObject("response").getJSONObject("results").getJSONObject("result").getJSONObject("address").getString("street");
                            String city = response.getJSONObject("SearchResults:searchresults").getJSONObject("response").getJSONObject("results").getJSONObject("result").getJSONObject("address").getString("city");
                            String state = response.getJSONObject("SearchResults:searchresults").getJSONObject("response").getJSONObject("results").getJSONObject("result").getJSONObject("address").getString("state");
                            String builtYear = response.getJSONObject("SearchResults:searchresults").getJSONObject("response").getJSONObject("results").getJSONObject("result").getString("yearBuilt");
                            String lotSize = response.getJSONObject("SearchResults:searchresults").getJSONObject("response").getJSONObject("results").getJSONObject("result").getString("lotSizeSqFt");
                            String sqFeet = response.getJSONObject("SearchResults:searchresults").getJSONObject("response").getJSONObject("results").getJSONObject("result").getString("finishedSqFt");
                            String bath = response.getJSONObject("SearchResults:searchresults").getJSONObject("response").getJSONObject("results").getJSONObject("result").getString("bathrooms");
                            String bed = response.getJSONObject("SearchResults:searchresults").getJSONObject("response").getJSONObject("results").getJSONObject("result").getString("bedrooms");
                            String value = response.getJSONObject("SearchResults:searchresults").getJSONObject("response").getJSONObject("results").getJSONObject("result").getJSONObject("zestimate").getJSONObject("amount").getString("$t");

                            String output = address + ' '+ city + ','+' ' + state;
                            String values = '$' + value;
                            GPSResults.setText(output);
                            Value.setText(values);
//                            locationManager.removeUpdates(listener);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){

                    }
                });
                requestQueue.add(request);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }

        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        GPSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates("gps", 5000, 0, listener);
//                Intent intent = new Intent(this, DisplayMessageActivity.class);
//                EditText editText = (EditText) findViewById(R.id.edit_message);
//                String message = editText.getText().toString();
//                intent.putExtra(EXTRA_MESSAGE, message);
//                startActivity(intent);
            }
        });
    }
}
