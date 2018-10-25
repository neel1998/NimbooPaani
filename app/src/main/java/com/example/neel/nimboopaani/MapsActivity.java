package com.example.neel.nimboopaani;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.MANAGE_DOCUMENTS;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int MIS_DIST=0;

    double currLat=0,currLon=0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        HttpTask httpTask = new HttpTask();
        httpTask.execute();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        mMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        }
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        currLat=locationGPS.getLatitude();
        currLon=locationGPS.getLongitude();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                Toast.makeText(MapsActivity.this,marker.getTag().toString(),Toast.LENGTH_SHORT).show();
                CampData campData=(CampData) marker.getTag();
                Intent i=new Intent(MapsActivity.this,CampActivity.class);
                i.putExtra("name",campData.getName());
                i.putExtra("capacity",campData.getCapacity());
                i.putExtra("contact",campData.getContact());
                i.putExtra("Lat",String.valueOf(currLat));
                i.putExtra("Lon",String.valueOf(currLon));
                startActivity(i);
                return true;
           }
        });



    }

    public class HttpTask extends AsyncTask<Void,Void,String> {

        String result_response="neel";
        @Override
        protected String doInBackground(Void... voids) {
            try {
                OkHttpClient client=new OkHttpClient();
                URL url=new URL("https://nimboopaani.azurewebsites.net/camps");
                Request request=new Request.Builder()
                        .url(url)
                        .build();
                Response response=client.newCall(request).execute();
                result_response=response.body().string();
                Log.d("",result_response);
            }
            catch (MalformedURLException e) {}
            catch (IOException e) {}
            return result_response;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONArray jsonArray=new JSONArray(s);
                LatLng pos=null;
                for(int i=0;i<jsonArray.length();++i){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    JSONObject data=jsonObject.getJSONObject("fields");
                    Double lat=Double.parseDouble(data.getString("lat"));
                    Double lon=Double.parseDouble(data.getString("lon"));
                    pos=new LatLng(lat,lon);
                    Marker marker=mMap.addMarker(new MarkerOptions().position(pos)
                            .title(data.getString("name")));

                    CampData campData=new CampData(data.getString("name"),data.getString("capacity"),data.getString("contact"));
                    marker.setTag(campData);
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}



