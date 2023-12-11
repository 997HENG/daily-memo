package com.example.daily_memo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        email = findViewById(R.id.emailForLogin);
        password = findViewById(R.id.passwordForLogin);
        login = findViewById(R.id.login);

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            private String txtEmail;
            private String txtPassword;

            @Override
            public void onClick(View v) {
                getEmailAndPassword();
                if(isEmailAndPasswordEmpty()){
                    emptyCredentials();
                    return;
                }
                loginUser();
            }

            private void getEmailAndPassword(){
                txtEmail = email.getText().toString();
                txtPassword = password.getText().toString();
            }

            private boolean isEmailAndPasswordEmpty (){
                return txtEmail == null || txtPassword == null || txtEmail.isEmpty() || txtPassword.isEmpty();
            }

            private void loginUser(){
                auth.signInWithEmailAndPassword(txtEmail,txtPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if(!isEmailVerified()){
                                emailNotVerifiedAndSendVerification();
                                return;
                            }
                            loginSuccessful();
                            toNotesActivity();
                            finish();
                        }
                    }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loginFailed();
                    }
                });

            }

            private boolean isEmailVerified(){
                return auth.getCurrentUser().isEmailVerified();
            }

            private void emailNotVerifiedAndSendVerification(){
                new AlertDialog.Builder(LoginActivity.this).setTitle("Email not Verified!")
                        .setMessage("Please verify your email first.\nOr check your email.")
                        .setPositiveButton("Send email verification", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sendEmailVerification();
                                    }
                                }
                        ).setNegativeButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }
                ).show();
            }

            private void emptyCredentials(){
                Toast.makeText(LoginActivity.this,"Empty credentials!",Toast.LENGTH_SHORT).show();
            }

            private void toNotesActivity(){
                startActivity(new Intent(LoginActivity.this,NotesActivity.class));
            }

            private void loginSuccessful(){
                Toast.makeText(LoginActivity.this,"Login successful!",Toast.LENGTH_SHORT).show();
            }

            private void sendEmailVerification(){
                auth.getCurrentUser().sendEmailVerification();
                Toast.makeText(LoginActivity.this,"Verification had been send!",Toast.LENGTH_SHORT).show();
            }

            private void userNotExistent(){
                Toast.makeText(LoginActivity.this,"User not existent!",Toast.LENGTH_SHORT).show();
            }
            private void loginFailed(){
                Toast.makeText(LoginActivity.this,"Login failed!\nCheck your email and password",Toast.LENGTH_SHORT).show();
            }


        });
    }
}