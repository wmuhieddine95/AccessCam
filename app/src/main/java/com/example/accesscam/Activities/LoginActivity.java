package com.example.accesscam.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.accesscam.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText username;
    EditText password;
    Button login;
    ProgressBar loading;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null)
        {
            Intent intent = new Intent(LoginActivity.this, Home.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loading = findViewById(R.id.loading);
        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user= username.getText().toString().trim();
                String pass= password.getText().toString().trim();
                login(user,pass);
            }});
            }//Close Create

    private void displayMessage(Context context, String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public void login(String user, String pass){
        if(TextUtils.isEmpty(user))
            username.setError("Email Required");
        else if (TextUtils.isEmpty(pass))
            password.setError("Password didn't match");
        loading.setVisibility(View.VISIBLE);
        try {
            mAuth.signInWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, Home.class);
                        startActivity(intent);
                        finish();
                    } else
                    {
                        mAuth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(LoginActivity.this,
                                                    "Registered and Signed",Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent
                                                    (LoginActivity.this, Home.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(LoginActivity.this,"Error!"+ task.getException()
                                                    .getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }); } }
            });//close Authentication
        } //close Try
        catch (Exception e)
        { Toast.makeText(LoginActivity.this,"Error!"+ e.getMessage()
                ,Toast.LENGTH_SHORT).show();}
        loading.setVisibility(View.INVISIBLE);
        }//Close Login
}
