package com.example.loginstart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class addInvitees extends AppCompatActivity {
    EditText inviteeEmail;
    Button invAddButton;
    Button finished;
    private Button returnToDashBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_invitees);
        inviteeEmail = (EditText) findViewById(R.id.inviteeEmail);
        invAddButton = (Button) findViewById(R.id.invAddBtn);
        finished = (Button) findViewById(R.id.addingDone);
        returnToDashBtn = (Button) findViewById(R.id.dashReturn);
        returnToDashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(addInvitees.this, student.class));
            }
        });
        invAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String theEmail = inviteeEmail.getText().toString().trim();
                if (theEmail == null || theEmail.isEmpty() || !theEmail.contains("@")) {
                    inviteeEmail.setError("Valid Email Required.");
                    inviteeEmail.requestFocus();
                } else {
                    DatabaseReference eventDatabase = FirebaseDatabase.getInstance().getReference("Events");
                    eventDatabase.child(getIntent().getStringExtra("title")).child("invitees").child(theEmail.substring(0, theEmail.length() - 4).toLowerCase()).setValue(theEmail.toLowerCase()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(addInvitees.this, "Invitee Added.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(addInvitees.this, "Failed to add Invitee.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        finished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ofTypeUser = getIntent().getStringExtra("userType");
                switch (ofTypeUser) {
                    case "student":
                        startActivity(new Intent(addInvitees.this, student.class));
                        break;
                    case "teacher":
                        startActivity(new Intent(addInvitees.this, teacher.class));
                        break;
                }
            }
        });
    }
}