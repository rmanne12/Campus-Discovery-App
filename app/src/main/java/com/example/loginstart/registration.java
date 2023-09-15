package com.example.loginstart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class registration extends AppCompatActivity {
    private TextView titlebanner;
    private EditText name, email, password, adminCode;
    private RadioButton userType;
    private RadioButton adminBtn;
    private RadioGroup userOpt;
    private Button signUp;
    private FirebaseAuth authorizer;
    private Button exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        titlebanner = (TextView) findViewById(R.id.titlebanner);
        authorizer = FirebaseAuth.getInstance();
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.emailC);
        password = (EditText) findViewById(R.id.passwordC);
        userOpt = (RadioGroup) findViewById(R.id.userType);
        adminBtn = (RadioButton) findViewById(R.id.adminB);
        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminCode = (EditText) findViewById(R.id.admin);
                adminCode.setVisibility(View.VISIBLE);
            }
        });
        exitBtn = (Button) findViewById(R.id.quit);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(registration.this, MainActivity.class));
            }
        });
        signUp = (Button) findViewById(R.id.signUpButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userOpt.getCheckedRadioButtonId() != -1) {
                    userType = (RadioButton) findViewById(userOpt.getCheckedRadioButtonId());
                    if (userType.getText().toString().equals("Admin")) {
                        adminCode = (EditText) findViewById(R.id.admin);
                        if (adminCode.getText().toString().equals("1347MIRAJ")) {
                            register();
                        } else {
                            Toast.makeText(registration.this, "Invalid code. Try Again.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        register();
                    }
                }
            }
        });
    }

    private void register() {
        String fullName = name.getText().toString().trim();
        if (fullName == null || fullName.isEmpty()) {
            name.setError("Valid Full Name Required.");
            name.requestFocus();
        }
        String uEmail = email.getText().toString().trim();
        if (uEmail == null || uEmail.isEmpty() || !uEmail.contains("@")) {
            email.setError("Valid Email Required.");
            email.requestFocus();
        }
        String uPassword = password.getText().toString().trim();
        if (uPassword == null || uPassword.isEmpty()) {
            password.setError("Valid Password Required.");
            password.requestFocus();
        } else if (uPassword.length() < 6) {
            password.setError("Password must contain at least 6 characters.");
            password.requestFocus();
        }
        String type = userType.getText().toString().trim();
        System.out.println("Got all info.");
        authorizer.createUserWithEmailAndPassword(uEmail,uPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userInfo currUser = new userInfo(fullName, uEmail, type);
                    System.out.println("User Info made. !!!!!!!");
                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://campusdiscovery-d2e9f-default-rtdb.firebaseio.com/");
                    DatabaseReference ref = database.getReference("UserInfo");
                    ref.child(authorizer.getCurrentUser().getUid()).setValue(currUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(registration.this, "Successfully Registered", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(registration.this, login.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(registration.this, "Registration Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(registration.this, "Failed to register user.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}