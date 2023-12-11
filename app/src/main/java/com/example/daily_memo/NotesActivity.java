package com.example.daily_memo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {
    private FloatingActionButton button;
    private Button logOut;
    Spinner spn;
    String [] options = {"All","Job","Store","School"};
    String [] opt = {"all","job","store","school"};
    String option;
    FirebaseAuth auth;
    FirebaseFirestore fireStore;
    ArrayList<Notes> notes;
    ArrayList<Notes> notes_f;
    ListView lst;
    MyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        getSupportActionBar().hide();
        button = findViewById(R.id.toMap);
        logOut = findViewById(R.id.logOut);
        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        notes = new ArrayList<Notes>();
        notes_f = new ArrayList<Notes>();
        lst = (ListView)findViewById(R.id.listView);

        adapter= new MyAdapter(this);
        lst.setAdapter(adapter);
        spn = findViewById(R.id.spn);
        ArrayAdapter<String > adapter_s= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,options);
        adapter_s.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spn.setAdapter(adapter_s);
        option="All";
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                option=opt[position];
                notes_f.clear();
                notes.forEach((e) -> {
                    if (e.type.equals(option) ) {
                        notes_f.add(e);
                    }
                });
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude",notes_f.get(position).latitude);
                bundle.putDouble("longitude",notes_f.get(position).longitude);
                startActivity(new Intent(NotesActivity.this,MapsActivity.class).putExtras(bundle));
            }
        });


        lst.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude",notes_f.get(position).latitude);
                bundle.putDouble("longitude",notes_f.get(position).longitude);
                startActivity(new Intent(NotesActivity.this,NewNoteActivity.class).putExtras(bundle));
                return false;
            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesActivity.this,MapsActivity.class));
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(NotesActivity.this,MainActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


        fireStore.collection("notes").whereEqualTo("userId",auth.getCurrentUser().getUid()).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        notes.add(new Notes(doc.getData().get("type").toString(),doc.getData().get("toDo").toString(),
                                doc.getData().get("latitude").toString(),doc.getData().get("longitude").toString(),doc.getData().get("docId").toString()));
                    }
                    if (option=="all"){
                        notes_f.addAll(notes);
                    }else {
                        notes.forEach((e) -> {
                            if (e.type.equals(option) ) {
                                notes_f.add(e);
                            }
                        });

                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });







    }



    @Override
    protected void onStop() {
        super.onStop();
        notes_f.clear();
        notes.clear();
    }
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater myInflater;
        public MyAdapter(Context c){
            myInflater = LayoutInflater.from(c);

        }
        @Override
        public int getCount(){
            return notes_f.size();
        }
        @Override
        public Object getItem(int position){
            return notes_f.get(position);
        }
        @Override
        public long getItemId(int position){
            return position;
        }




        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            convertView = myInflater.inflate(R.layout.notes, null);


            ImageView typeLogo = (ImageView) convertView.findViewById(R.id.typeLogo);
            TextView toDoNote = ((TextView) convertView.findViewById(R.id.toDoNote));
            TextView longitude = ((TextView) convertView.findViewById(R.id.longitudeLs));
            TextView latitude = ((TextView) convertView.findViewById(R.id.latitudeLs));

            if(notes_f.size()==0){
                typeLogo.setImageResource(R.mipmap.ic_launcher);
                toDoNote.setText("Nothing here....");
            }else{

                switch (notes_f.get(position).type){
                    case "job":
                        typeLogo.setImageResource(R.drawable.job);
                        break;
                    case  "store":
                        typeLogo.setImageResource(R.drawable.store);
                        break;
                    case "school":
                        typeLogo.setImageResource(R.drawable.school);
                        break;
                }
                toDoNote.setText(notes_f.get(position).toDo);
                longitude.setText(String.format("%.3f",notes_f.get(position).longitude));
                latitude.setText(String.format("%.3f",notes_f.get(position).latitude));


            }


            return convertView;
        }
    }
}


