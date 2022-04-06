package com.example.qrhunter;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.qrhunter.databinding.ActivityMapDemo2Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * @description This java file displays QR codes near the QR code selected by the user.
 */
public class MapDemo2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapDemo2Binding binding;
    FirebaseFirestore db;
    double currentlng;
    double currentlat;
    String positionid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent =getIntent();
        positionid=intent.getStringExtra("strqrid");

        binding = ActivityMapDemo2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Button back = findViewById(R.id.goback2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     *
     * @param googleMap
     * @description to get the map ready
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * Add markers for the QR codes in the Firestore
     * Show the score of each QR code.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        db = FirebaseFirestore.getInstance();
        CollectionReference locationRef = db.collection("QRCodes");
        positionid = getIntent().getStringExtra("strqrid");
        Log.d(TAG,"得到的信息xxxxxxxxxxx"+positionid);

        HashScore hashScore = new HashScore();
        DocumentReference docUserRef = locationRef.document(hashScore.hash256(positionid));
        docUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                GeoPoint currentstr  = document.getGeoPoint("geoPoint");
                currentlat = currentstr.getLatitude();
                currentlng = currentstr.getLongitude();
                Log.d(TAG,"!!!!!!!!!!"+currentlat+"注意注意"+currentlng);
                LatLng latLng = new LatLng(currentlat, currentlng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        });

        locationRef
                .whereEqualTo("sharedLocation", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "onSuccess: getting the data");
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : snapshotList) {
                            Log.d(TAG, "onSuccess: " + snapshot.getGeoPoint("geoPoint"));
                            GeoPoint geoPoint = snapshot.getGeoPoint("geoPoint");
                            double lat = geoPoint.getLatitude();
                            double lng = geoPoint.getLongitude();
                            LatLng latLng = new LatLng(lat, lng);
                            double qrScore = snapshot.getDouble("score");
                            // calculate the distance between current location and markers
                            mMap.addMarker(new MarkerOptions().position(latLng).title(snapshot.getString("qrid")).snippet(" Score: "+qrScore));
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            mMap.getUiSettings().setZoomControlsEnabled(true);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });


    }
}
