package com.example.loginstart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {
    private TextView register;
    private EditText email;
    private EditText password;
    private Button logInBtn;
    private Button exitBtn;
    private FirebaseAuth logAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register = (TextView) findViewById(R.id.registerLink);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int viewID = view.getId();
                if (viewID == R.id.registerLink) {
                    openRegistration();
                }
            }
        });
        exitBtn = (Button) findViewById(R.id.quit);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login.this, MainActivity.class));
            }
        });
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        logAuth = FirebaseAuth.getInstance();
        logInBtn = (Button) findViewById(R.id.logInButton);
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLogIn();
            }
        });
    }

    private void openRegistration() {
        Intent goRegister = new Intent(this, registration.class);
        System.out.println("Going to register.");
        startActivity(goRegister);
    }
    private void startLogIn() {
        System.out.println("Starting LOGIN");
        String uEmail = email.getText().toString().trim();
        System.out.println("\"" + uEmail + "\"");
        String uPass = password.getText().toString().trim();
        System.out.println("\"" + uPass + "\"");
        if (uEmail == null || uEmail.isEmpty() || !uEmail.contains("@")) {
            email.setError("Valid Email Required.");
            email.requestFocus();
        }
        if (uPass == null || uPass.isEmpty()) {
            password.setError("Valid Password Required.");
            password.requestFocus();
        }
        System.out.println("Got to preLogin.");
        logAuth.signInWithEmailAndPassword(uEmail, uPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    System.out.println("Signed in");
                    Intent welcoming = new Intent(login.this, welcome.class);
                    startActivity(welcoming);
                } else {
                    Toast.makeText(login.this, "Login failed. Try Again.", Toast.LENGTH_LONG);
                }
            }
        });
    }
}