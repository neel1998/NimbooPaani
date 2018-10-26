package com.example.neel.nimboopaani;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CampActivity extends AppCompatActivity {

    TextView nameView, contactView, capacityView;
    Button callButton,smsButton,addButton;
    String contact="",currLat="0",currLon="0",ppl="Please Send Help";
    EditText editPeople;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camp);
        nameView = findViewById(R.id.name);
        capacityView = findViewById(R.id.capacity);
        contactView = findViewById(R.id.contact);
        contact=getIntent().getStringExtra("contact");
        nameView.setText("Name: " + getIntent().getStringExtra("name"));
        capacityView.setText("Capacity: " + getIntent().getStringExtra("capacity"));
        contactView.setText("Contact: " + getIntent().getStringExtra("contact"));

        currLat=getIntent().getStringExtra("Lat");
        currLon=getIntent().getStringExtra("Lon");

        editPeople=findViewById(R.id.edit_ppl);
        editPeople.setVisibility(View.GONE);

        callButton=findViewById(R.id.call_btn);
        callButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + contact));
                    if (ActivityCompat.checkSelfPermission(CampActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE},1);
                        return; }
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }
            }
        });

        smsButton=findViewById(R.id.sms_btn);
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setData(Uri.parse("sms:"));
                smsIntent.putExtra("address", contact);
                smsIntent.putExtra("sms_body","Latitude: "+currLat
                        +"\nLongitude: "+currLon+"\nPlease Send Help at these Coordinates");
                if(!editPeople.getText().toString().equals("")){
                    ppl=editPeople.getText().toString();
                }

                AddPeopleTask addPeopleTask=new AddPeopleTask();
                addPeopleTask.execute();
                startActivity(smsIntent);
            }
        });

        addButton=findViewById(R.id.add_btn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPeople.setVisibility(View.VISIBLE);
            }
        });
    }
    public class AddPeopleTask extends AsyncTask<Void,Void,Void> {

        JSONObject jsonObject=new JSONObject();
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                jsonObject.put("lat",currLat);
                jsonObject.put("lon",currLon);
                jsonObject.put("ppl",ppl);

//                URL url=new URL("http://nimboopaani.azurewebsites.net/rescueadd");
                URL url=new URL("http://10.1.134.235:8000/rescueadd");
                final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
                OkHttpClient client=new OkHttpClient();
                RequestBody body= RequestBody.create(JSON,jsonObject.toString());
                Request request=new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response=client.newCall(request).execute();
//                Toast.makeText(CampActivity.this,response.body().string(), Toast.LENGTH_SHORT).show();
            }
            catch (MalformedURLException e) {}
            catch (IOException e) {}
            catch (JSONException e) {}
            return null;
        }
    }

}
