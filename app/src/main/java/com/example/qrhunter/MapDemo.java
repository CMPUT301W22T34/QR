package com.example.qrhunter;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.qrhunter.databinding.ActivityMapDemoBinding;
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
 *@description This file is mainly responsible for searching on the map and moving the camera to the searched location.
 */
public class MapDemo extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapDemoBinding binding;
    FirebaseFirestore db;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    List<Address> listGeoCoder;
    double currentlatitude;
    double currentlongitude;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button back = findViewById(R.id.goback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        /**
         * @description Obtain the SupportMapFragment and get notified when the map is ready to be used.
         *
         * At the same time, search for nearby QR codes and display them on the map.
         * */

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SearchView searchView = (SearchView) findViewById(R.id.txtSearchCode);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //search location to find QRcodes nearby
                try {
                    listGeoCoder = new Geocoder(MapDemo.this).getFromLocationName(query, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                double lng = listGeoCoder.get(0).getLongitude();
                double lat = listGeoCoder.get(0).getLatitude();
                Log.i("GoogleMap", "Longitude: " + String.valueOf(lng) + " Latitude: " + String.valueOf(lat));
                LatLng latLng = new LatLng(lat, lng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                //mMap.animateCamera(CameraUpdateFactory.zoomIn());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && newText.length() > 0) {
                    Log.d("", "text change");
                }
                return true;
            }
        });
    }


    /**
     *
     * @link #onMapReady(GoogleMap)
     * @param googleMap
     * @description This function is mainly responsible for the preparation of the map before it is displayed.
     *  This callback is triggered when the map is ready to be used.
     *  This is where we can add markers from the firestore, add listeners or move the camera.
     *  We add multiple markers from the firestore.The markers represent QR codes.
     *  Obtain permission for the app to access the location of the phone.
     *  Get the user's current location and display QR codes near them.
     *  Search for QR codes entered by the user and display QR codes near the searched location
     *  Display the distance of each QR code to the user's current location
     * Show the score of each QR code
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(53.54237203764759, -113.49141013307299);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserlocation();
            getcurrentlocationcoordinatestr();

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }
        }


        //implement multiple markers on the map
        db = FirebaseFirestore.getInstance();
        CollectionReference locationRef = db.collection("QRCodes");
        locationRef
                .whereEqualTo("sharedLocation", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "onSuccess: getting the data");
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : snapshotList) {
                            //Log.d(TAG, "onSuccess: " + snapshot.getData().toString());
                            Log.d(TAG, "onSuccess: " + snapshot.getGeoPoint("geoPoint"));
                            GeoPoint geoPoint = snapshot.getGeoPoint("geoPoint");
                            double lat = geoPoint.getLatitude();
                            double lng = geoPoint.getLongitude();
                            LatLng latLng = new LatLng(lat, lng);
                            double qrScore = snapshot.getDouble("score");
                            // calculate the distance between current location and markers
                            float results[]=new float[10];
                            Location.distanceBetween(currentlatitude,currentlongitude,lat,lng,results);
                            mMap.addMarker(new MarkerOptions().position(latLng).title(snapshot.getString("qrid")).snippet(" Score: "+qrScore+"  Distance: "+results[0]+" meters"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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

    /**
     * @description get player current location
     * @link enableUserLocation
     * @param
     */
    private void enableUserlocation() {
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
        mMap.setMyLocationEnabled(true);
    }

    /**
     * @description Get the latitude and longitude of the current position to calculate the distance
     *
     * @param
     */
    private void getcurrentlocationcoordinatestr() {
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
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng=new LatLng(location.getLatitude(), location.getLongitude());
                currentlatitude=latLng.latitude;
                currentlongitude=latLng.longitude;
                Log.d(TAG, "当前位置的经纬度"+ currentlatitude+" "+currentlongitude);
            }
        });

    }

    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserlocation();
                getcurrentlocationcoordinatestr();
            }else{

            }
        }
    }
}