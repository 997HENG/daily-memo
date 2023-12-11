package com.example.daily_memo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button login;
    private Button register;
    private FirebaseAuth auth;
    private ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        logo = findViewById(R.id.logo);
        login = findViewById(R.id.toLogin);
        register = findViewById(R.id.toRegister);
        registerForContextMenu(logo);

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(isLogin()){
            startActivity(new Intent(MainActivity.this,NotesActivity.class));
            finish();
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu,menu);
        menu.setHeaderTitle("Daily Memo");


    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.about:{
                new AlertDialog.Builder(MainActivity.this).setTitle("about").setMessage("author:Heng\nid:U1024026").setPositiveButton(
                        "OK",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }
                ).show();
            }
            break;
            case R.id.github:{
                new AlertDialog.Builder(MainActivity.this).setTitle("github").setMessage("name:997heng\nrepository:https://github.com/997HENG/daily-memo").setPositiveButton(
                        "OK",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/997HENG/daily-memo")));
                                dialogInterface.dismiss();
                            }
                        }
                ).show();
            }
            break;
            case R.id.exit:{
                finish();
                break;
            }
        }
        return super.onContextItemSelected(item);
    }

    public boolean isLogin(){
        return auth.getCurrentUser() != null;
    }
}