package com.example.daily_memo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class NewNoteActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Intent intent;
    private Bundle bundle;
    private GoogleMap mMap;
    private RadioGroup group;
    private RadioButton jobb;
    private RadioButton storeb;
    private  RadioButton schoolb;
    private TextInputEditText text;
    private String toDo;
    private String type;
    private ImageButton ok;
    private ImageButton no;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private Double  docId;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        auth= FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        group = findViewById(R.id.group);
        text = findViewById(R.id.textField);
        jobb = findViewById(R.id.job);
        storeb = findViewById(R.id.store);
        schoolb = findViewById(R.id.school);
        ok = findViewById(R.id.ok);
        no = findViewById(R.id.no);
        text.setText("");

        flag =true;




        intent= this.getIntent();
        bundle = intent.getExtras();
        docId =bundle.getDouble("latitude") + bundle.getDouble("longitude");
        firestore.collection("notes").document(docId.toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        flag = false;
                        text.setText(doc.getData().get("toDo").toString());
                        switch (doc.getData().get("type").toString()){
                            case "job":
                                jobb.setChecked(true);
                                break;
                            case "store":
                                storeb.setChecked(true);
                                break;
                            case "school":
                                schoolb.setChecked(true);
                                break;
                        }
                    }
                }
            }
        });







        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDo = text.getText().toString();
                Map<String, Object> note = new HashMap<>();
                if (type == null) {
                    Toast.makeText(NewNoteActivity.this, "Type should not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                note.put("toDo", toDo);
                note.put("type", type);
                note.put("latitude", bundle.getDouble("latitude"));
                note.put("longitude", bundle.getDouble("longitude"));
                note.put("userId", auth.getCurrentUser().getUid());

                note.put("docId", docId);








                if (flag){
                    firestore.collection("notes").document(docId.toString()).set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(NewNoteActivity.this, "added!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    System.out.println("-----------------------------------------------------------------------------------------------");
                    firestore.collection("notes").document(docId.toString()).update(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(NewNoteActivity.this, "updated!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }





                finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag){
                    firestore.collection("notes").document(docId.toString()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(NewNoteActivity.this, "deleted!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                finish();
            }
        });


    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        LatLng location = new LatLng(
                bundle.getDouble("latitude"), bundle.getDouble("longitude"));
        Marker marker = mMap.addMarker(new MarkerOptions().position(location));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {



                switch (checkedId){
                    case R.id.job:
                        type = "job";
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(location).title("Job")
                                .icon((BitmapDescriptorFactory.fromResource(R.drawable.job))).alpha(0.5f));
                        break;
                    case R.id.store:
                        type = "store";
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(location).title("Job")
                                .icon((BitmapDescriptorFactory.fromResource(R.drawable.store))).alpha(0.5f));
                        break;
                    case R.id.school:
                        type="school";
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(location).title("Job")
                                .icon((BitmapDescriptorFactory.fromResource(R.drawable.school))).alpha(0.5f));
                        break;
                }

            }

        });



    }
}