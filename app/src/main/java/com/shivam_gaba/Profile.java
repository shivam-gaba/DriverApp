package com.shivam_gaba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.http.Url;

public class Profile extends AppCompatActivity {

    TextView driverName, truckNumber, emailId, driverPhoneNumber;
    ImageView driverPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        driverName = findViewById(R.id.driverName);
        truckNumber = findViewById(R.id.truckNumber);
        emailId = findViewById(R.id.emailId);
        driverPhoneNumber = findViewById(R.id.driverPhoneNumber);
        driverPic = findViewById(R.id.driverPic);

        driverName.setText("Name : " + getIntent().getStringExtra("driverName"));
        driverPhoneNumber.setText("Contact Number : " + getIntent().getStringExtra("driverPhoneNumber"));
        truckNumber.setText("Truck Number : " + getIntent().getStringExtra("truckNumber"));
        emailId.setText("Email Id : " + getIntent().getStringExtra("emailId"));
        Glide.with(getApplicationContext()).load(getIntent().getStringExtra("driverPicUrl")).into(driverPic);
    }
}