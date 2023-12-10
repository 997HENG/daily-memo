package com.example.daily_memo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
    FirebaseAuth auth;
    FirebaseFirestore fireStore;
    List<Notes> notes;
    ListView lst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        button = findViewById(R.id.toMap);
        logOut = findViewById(R.id.logOut);
        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        notes = new ArrayList<Notes>();
        lst = (ListView)findViewById(R.id.listView);


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
                        System.out.println(doc.getData()+"--------------------------");
                        notes.add(new Notes(doc.getData().get("type").toString(),doc.getData().get("toDo").toString(),
                                doc.getData().get("latitude").toString(),doc.getData().get("longitude").toString(),doc.getData().get("docId").toString()));
                    }
                }
            }
        });
    }
}

public class MyAdapter extends BaseAdapter {
    private LayoutInflater myInflater;
    public MyAdapter(Context c){
        myInflater = LayoutInflater.from(c);
    }
    @Override
    public int getCount(){
        return 0;
    }
    @Override
    public Object getItem(int position){
        return null;
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        convertView = myInflater.inflate(R.layout.notes, null);

        // 取得 mylayout.xml 中的元件
        ImageView typeLogo = (ImageView) convertView.findViewById(R.id.typeLogo);
        TextView typeNote = ((TextView) convertView.findViewById(R.id.typeNote));
        TextView toDoNote = ((TextView) convertView.findViewById(R.id.toDoNote));


        // 設定元件內容
        typeLogo

        return convertView;
    }
}
}