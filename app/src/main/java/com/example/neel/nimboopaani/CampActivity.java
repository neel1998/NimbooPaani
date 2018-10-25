package com.example.neel.nimboopaani;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CampActivity extends AppCompatActivity {

    TextView nameView, contactView, capacityView;
    Button callButton,smsButton;
    String contact="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camp);
        nameView = findViewById(R.id.name);
        capacityView = findViewById(R.id.capacity);
        contactView = findViewById(R.id.contact);
        contact=getIntent().getStringExtra("contact");
        nameView.setText("Name" + getIntent().getStringExtra("name"));
        capacityView.setText("Capacity:" + getIntent().getStringExtra("capacity"));
        contactView.setText("Contact:" + getIntent().getStringExtra("contact"));

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
//                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.setData(Uri.parse("sms:"));
                smsIntent.putExtra("address", contact);
                smsIntent.putExtra("sms_body","Plz Send Help");
                startActivity(smsIntent);


            }
        });
    }
}
