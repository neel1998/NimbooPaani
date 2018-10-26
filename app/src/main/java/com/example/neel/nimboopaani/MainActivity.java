package com.example.neel.nimboopaani;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private static final double AVG_ALT=8;
    double currAlt=0,currLat=0,currLon=0;
    RelativeLayout relativeLayout;
    TextView placesText;
    ArrayList<PlacesData> placesList;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView altTxt=findViewById(R.id.alt_text);
        TextView safeTxt=findViewById(R.id.safe_text);
        relativeLayout=findViewById(R.id.relative_layout);
        placesText=findViewById(R.id.places_text);

        placesList=new ArrayList<>();

        PlacesTask placesTask=new PlacesTask();
        placesTask.execute();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        }
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        currAlt=locationGPS.getAltitude();
        currLat=locationGPS.getLatitude();
        currLon=locationGPS.getLongitude();
        altTxt.setText("You are at Altitude of "+String.valueOf(currAlt)+" meter");

        if(currAlt>=AVG_ALT) {
            relativeLayout.setBackgroundColor(Color.parseColor("#8bc34a"));
            safeTxt.setText("You are at Safe Altitude");
        }
        else{
            relativeLayout.setBackgroundColor(Color.parseColor("#f44336"));
            safeTxt.setText("You are not at Safe Altitude");
        }

        Button maps_btn=findViewById(R.id.maps_btn);
        maps_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,MapsActivity.class);
                startActivity(i);
            }
        });


    }

    public class PlacesTask extends AsyncTask<Void,Void,String> {

        String result_response = "response";

        @Override
        protected String doInBackground(Void... voids) {
            try {
//                URL url = new URL("http://nimboopaani.azurewebsites.net/places");
                URL url=new URL("http://10.1.134.235:8000/places");
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = client.newCall(request).execute();
                result_response = response.body().string();
                Log.d("",result_response);
            } catch (MalformedURLException e) {
            } catch (IOException e) {
            }
            return result_response;
        }

        @Override
        protected void onPostExecute(String s) {
            String data="ForeCast\n\n";
            try {
                JSONArray jsonArray=new JSONArray(s);
                for(int i=0;i<jsonArray.length();++i){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    double dist=distance(currLat,currLon,Double.valueOf(jsonObject.getString("Lat")),Double.valueOf(jsonObject.getString("Lon")));
                    placesList.add(
                            new PlacesData(jsonObject.getString("Name"),
                            jsonObject.getString("Lat"),
                            jsonObject.getString("Lon"),
                                    dist));
                    data+=jsonObject.getString("Name")+"\t\t"+dist+" km\n";
                }
                placesText.setText(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
