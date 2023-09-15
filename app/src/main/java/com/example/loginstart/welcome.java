package com.example.loginstart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class welcome extends AppCompatActivity {
    private FirebaseUser user;
    private DatabaseReference users;
    private String identification;
    private TextView userName;
    private TextView userStatus;
    private Timer delay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        user = FirebaseAuth.getInstance().getCurrentUser();
        users = FirebaseDatabase.getInstance().getReference("UserInfo");
        identification = user.getUid();
        userName = (TextView) findViewById(R.id.userName);
        userStatus = (TextView) findViewById(R.id.userStatus);
        users.child(identification).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo currUser = snapshot.getValue(userInfo.class);
                currUser.printOut();
                if (currUser != null) {
                    System.out.println(currUser.getFullName());
                    System.out.println(currUser.getUserType());
                    userName.setText(currUser.getFullName());
                    userStatus.setText(currUser.getUserType());
                    try {
                        delay = new Timer();
                        eventDashboard(currUser.getUserType());
                    } catch (InterruptedException e) {
                        Toast.makeText(welcome.this, "ERROR OCCURRED", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(welcome.this, "ERROR", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void eventDashboard(String userType) throws InterruptedException {
        delay.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent dashboard = new Intent(welcome.this, student.class);
                dashboard.putExtra("category", "Default");
                dashboard.putExtra("subcategory", "Default");
                        startActivity(dashboard);
            }
        }, 1500);
    }
}