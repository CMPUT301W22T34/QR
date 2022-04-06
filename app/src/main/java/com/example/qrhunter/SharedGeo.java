package com.example.qrhunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @description This java file is to ask the users if they want to share the geolocation
 */
public class SharedGeo extends AppCompatActivity {
    FirebaseFirestore db;
    String qrCode;
    int LOCATION_REQUEST_CODE = 10002;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentlatitude1;
    double currentlongitude1;
    GeoPoint geo = new GeoPoint(0.1, 0.2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_geo);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //back button
        Button back = findViewById(R.id.btnBackToScan);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SharedGeo.this, MainActivity.class);
                startActivity(intent);
            }
        });

        SharedData appData = (SharedData) getApplication();
        qrCode = appData.getQrcode();
        // The button not sharing the geolocation
        TextView notBtn = findViewById(R.id.btntxt);
        notBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = FirebaseFirestore.getInstance();
                HashScore hashScore = new HashScore();
                CollectionReference codesRef = db.collection("QRCodes");
                //DocumentReference docCodeRef = codesRef.document(qrCode);
                DocumentReference docCodeRef = codesRef.document(hashScore.hash256(qrCode));
                docCodeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> data = new HashMap<>();
                            data.put("sharedLocation", false);
                            docCodeRef.set(data, SetOptions.merge());
                        }
                    }
                });
                Intent intent = new Intent(SharedGeo.this, UserCode.class);
                startActivity(intent);

            }
        });

        //update geo point of the code
        Button share = findViewById(R.id.btnTake);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SharedGeo.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                    Intent intent = new Intent(SharedGeo.this, UserCode.class);
                    startActivity(intent);
                } else {
                    askLocationPermission();

                }
            }
        });


    }

    /**
     * @description get the current location
     */
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        db = FirebaseFirestore.getInstance();
        HashScore hashScore = new HashScore();
        CollectionReference codesRef = db.collection("QRCodes");
        DocumentReference docCodeRef = codesRef.document(hashScore.hash256(qrCode));
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if( location !=null){
                    currentlatitude1=location.getLatitude();
                    currentlongitude1=location.getLongitude();
                    Log.d("", "sssssssiiiiiihhhhheeeee"+currentlongitude1+",,,,"+currentlatitude1);
                    com.google.android.gms.maps.model.LatLng latLng = new LatLng(currentlatitude1, currentlongitude1);
                    docCodeRef.update("sharedLocation",true);
                    //update the geo point
                    geo = new GeoPoint(currentlatitude1,currentlongitude1);
                    docCodeRef.update("geoPoint", geo);

                }else{
                    Log.d("fail","Success:Location was null");
                }
            }
        });
        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fail","fail"+e.getLocalizedMessage());
            }
        });

    }

    /**
     * @description ask the user for Location Permission
     */
    private void askLocationPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            }
        }
    }

    /**
     * @description ask the user for Location Permission second time
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==LOCATION_REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLastLocation();

            }else{

            }
        }
    }
}
