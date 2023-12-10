package com.example.daily_memo;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener ,GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private LocationManager mgr;
    private LatLng currentLocation;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    FirebaseAuth auth;
    FirebaseFirestore fireStore;
    List<Notes> notes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        notes = new ArrayList<Notes>();

        checkPermission();
    }

    @Override
    protected void onResume() {

        super.onResume();
        enableLocationUpdates(true);

        fireStore.collection("notes").whereEqualTo("userId",auth.getCurrentUser().getUid()).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        System.out.println(doc.getData()+"--------------------------");
                        notes.add(new Notes(doc.getData().get("type").toString(),doc.getData().get("toDo").toString(),
                                doc.getData().get("latitude").toString(),doc.getData().get("longitude").toString(),doc.getData().get("docId").toString()));
                    }

                    if(!notes.isEmpty()){
                        for(Notes note : notes){
                            LatLng location = new LatLng(note.latitude,note.longitude);
                            switch (note.type){
                                case "job":
                                    mMap.addMarker(new MarkerOptions().position(location).title("Job")
                                            .icon((BitmapDescriptorFactory.fromResource(R.drawable.job))).alpha(0.5f).snippet(note.toDo));
                                    break;
                                case "store":
                                    mMap.addMarker(new MarkerOptions().position(location).title("Store")
                                            .icon((BitmapDescriptorFactory.fromResource(R.drawable.store))).alpha(0.5f).snippet(note.toDo));
                                    break;
                                case "school":
                                    mMap.addMarker(new MarkerOptions().position(location).title("School")
                                            .icon((BitmapDescriptorFactory.fromResource(R.drawable.school))).alpha(0.5f).snippet(note.toDo));
                                    break;

                            }
                        }

                    }
                }
            }
        });



    }

    @Override
    protected void onPause() {
        super.onPause();
        enableLocationUpdates(false);
        notes.clear();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED){
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude",latLng.latitude);
                bundle.putDouble("longitude",latLng.longitude);
                startActivity(new Intent(MapsActivity.this,NewNoteActivity.class).putExtras(bundle));
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                marker.remove();
                Bundle bundle = new Bundle();

                bundle.putDouble("latitude",marker.getPosition().latitude);
                bundle.putDouble("longitude",marker.getPosition().longitude);
                startActivity(new Intent(MapsActivity.this,NewNoteActivity.class).putExtras(bundle));
                return false;
            }
        });
        LatLng miaoli = new LatLng(
                24.57, 120.82);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(miaoli));


    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
        if(mMap!=null){
            mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )
        != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==200){
            if(grantResults.length>=1 && grantResults[0]!= PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Please enable location access!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableLocationUpdates(boolean isTurnOn){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(isTurnOn){
                isGPSEnabled = mgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if(!isGPSEnabled && !isNetworkEnabled){
                    Toast.makeText(this,"Please check location access!",Toast.LENGTH_LONG).show();
                }else {
                    if(isGPSEnabled){
                        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,5,this);
                    }
                    if(isNetworkEnabled){
                        mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,3000,5,this);
                    }
                }

            }else {
                mgr.removeUpdates(this);
            }
        }
    }

}