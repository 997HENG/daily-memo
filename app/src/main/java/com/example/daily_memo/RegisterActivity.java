package com.example.daily_memo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button register;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        email = findViewById(R.id.emailForRegister);
        password = findViewById(R.id.passwordForRegister);
        register = findViewById(R.id.register);
        auth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {

            private String txtEmail = "";
            private String txtPassword = "";

            @Override
            public void onClick(View v) {
                txtEmail = email.getText().toString();
                txtPassword = password.getText().toString();

                if(isEmailAndPasswordEmpty(txtEmail,txtPassword)){

                    emptyCredentials();
                    return;
                }
                if(isPasswordTooShort(txtPassword)){
                    passwordTooShort();
                    return;
                }
                registerUser(txtEmail,txtPassword);
            }

            private boolean isEmailAndPasswordEmpty (String email,String password){
                return email.isEmpty() || password.isEmpty();
            }

            private boolean isPasswordTooShort (String password){
                return (password.length()<6);
            }

            private void registerUser(String email,String password){
                auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(RegisterActivity.this,
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            registerUserSuccessful();
                                            sendEmailVerification(auth);
                                        }else{
                                            registrationFailed();
                                        }
                                    }
                                }
                        );
            }

            private void sendEmailVerification(FirebaseAuth auth){
                auth.getCurrentUser().sendEmailVerification();
            }

            private void registerUserSuccessful(){
                Toast.makeText(RegisterActivity.this,"Register user successful!",Toast.LENGTH_SHORT).show();
            }

            private void registrationFailed(){
                Toast.makeText(RegisterActivity.this,"Registration failed!",Toast.LENGTH_SHORT).show();
            }

            private void emptyCredentials(){
                Toast.makeText(RegisterActivity.this,"Empty credentials!",Toast.LENGTH_SHORT).show();
            }

            private void passwordTooShort(){
                Toast.makeText(RegisterActivity.this,"Password too short(<6)!",Toast.LENGTH_SHORT).show();
            }

        });

    }
}