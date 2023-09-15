package com.example.loginstart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class showAttendees extends AppCompatActivity implements RecyclerViewInterface {
    private Button returnToDashBtn;
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendees_list);

        TabLayout tabs = findViewById(R.id.tabLayout);

        String host = getIntent().getStringExtra("host");
        String title = getIntent().getStringExtra("title");

        DatabaseReference eventDatabase = FirebaseDatabase.getInstance().getReference("Events");
        DatabaseReference attendees = eventDatabase.child(title).child("attendees");

        attendees.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, ArrayList<DataSnapshot>> map = new HashMap<>();
                ArrayList<DataSnapshot> attendees = new ArrayList<>();
                ArrayList<DataSnapshot> maybes = new ArrayList<>();
                ArrayList<DataSnapshot> wonts = new ArrayList<>();
                ArrayList<DataSnapshot> enemies = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.child("Will Attend").getChildren()) {
                    attendees.add(dataSnapshot);
                }
                for (DataSnapshot dataSnapshot : snapshot.child("Maybe").getChildren()) {
                    maybes.add(dataSnapshot);
                }
                for (DataSnapshot dataSnapshot : snapshot.child("Won't Attend").getChildren()) {
                    wonts.add(dataSnapshot);
                }
                for (DataSnapshot dataSnapshot : snapshot.child("I'm praying on ur downfall").getChildren()) {
                    enemies.add(dataSnapshot);
                }
                System.out.println("Will: " + attendees);
                System.out.println("Maybe: " + maybes);
                System.out.println("Won't: " + wonts);
                System.out.println("Enemies: " + enemies);
                map.put("Will Attend", attendees);
                map.put("Maybe", maybes);
                map.put("Won't Attend", wonts);
                map.put("I'm praying on ur downfall", enemies);


                RecyclerView recyclerView = findViewById(R.id.attendeesRecyclerview);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(showAttendees.this);
                recyclerView.setLayoutManager(linearLayoutManager);
                RecyclerViewAdapter2 recyclerViewAdapter2 = new RecyclerViewAdapter2(title, host, attendees, context, showAttendees.this);
                recyclerView.setAdapter(recyclerViewAdapter2);

                tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        ArrayList<DataSnapshot> childData = new ArrayList<>();
                        int position = tab.getPosition();
                        if(position==0){
                            childData = attendees;
                        }else if(position==1){
                            childData = maybes;
                        }else if(position==2){
                            childData = wonts;
                        }else if(position==3){
                            childData = enemies;
                        }
                        RecyclerView recyclerView = findViewById(R.id.attendeesRecyclerview);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(showAttendees.this);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        RecyclerViewAdapter2 recyclerViewAdapter2 = new RecyclerViewAdapter2(title, host, childData, context, showAttendees.this);
                        recyclerView.setAdapter(recyclerViewAdapter2);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Failed to retrieve attendees");
            }
        });
        returnToDashBtn = (Button) findViewById(R.id.dashReturn);
        returnToDashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(showAttendees.this, student.class));
            }
        });
    }

    @Override
    public void onClickEdit(List<DataSnapshot> data, int position) {

    }

    @Override
    public void onItemClick(List<DataSnapshot> data, int position) {

    }
}
