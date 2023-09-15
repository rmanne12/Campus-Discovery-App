package com.example.loginstart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class student extends AppCompatActivity implements RecyclerViewInterface {
    private Button exitBtn;
    private FloatingActionButton createEventBtn;
    private FloatingActionButton mapBtn;
    private DatabaseReference mirajDatabase;
    private FirebaseUser currUser;
    private TextView dashboardHeader;
    private userInfo currUserInfo;
    private Button nextBtn;
    private Button backBtn;
    private String category;
    private String subcategory;
    private FloatingActionButton filter;
    private ArrayAdapter<String> filterAdapter;
    private String[] filterOpts = new String[4];
    private String[] locationOpts;
    private Button myEventBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        category = getIntent().getStringExtra("category");
        subcategory = getIntent().getStringExtra("subcategory");
        if (category == null) {
            category = "Default";
            subcategory = "Default";
        }
        /*//filter code
        filter = (Spinner) findViewById(R.id.filter);
        filterOpts[0] = "Default"; filterOpts[1] = "Location"; filterOpts[2] = "Date"; filterOpts[3] = "Host";
        locationOpts = new String[]{"West Dorms", "CRC", "CRC Field", "Student Center",
                "Tech Green", "CULC", "Klaus", "CoC", "East Dorms", "NAve", "Bobby Dodd Stadium", "McCamish Pavilion", "Tech Square"};
        filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filterOpts);
        filter.setAdapter(filterAdapter);
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(view.toString().trim()) {
                    case 0:
                        break;
                    case 1:
                        filterAdapter = new ArrayAdapter<>(student.this, android.R.layout.simple_spinner_dropdown_item, locationOpts);
                        filter.setAdapter(filterAdapter);
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        //end filter code*/
        mirajDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        mirajDatabase.child(currUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currUserInfo = snapshot.getValue(userInfo.class);
                if (currUserInfo != null) {
                    if (currUserInfo.getUserType().equals("Admin")) {
                        dashboardHeader = (TextView)  findViewById(R.id.pageHeader);
                        dashboardHeader.setText("Admin Dashboard");
                        FloatingActionButton tempBtn = (FloatingActionButton) findViewById(R.id.studentCreate);
                        tempBtn.setVisibility(View.GONE);
                    } else if (currUserInfo.getUserType().equals("Teacher")) {
                        dashboardHeader = (TextView) findViewById(R.id.pageHeader);
                        dashboardHeader.setText("Teacher Dashboard");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        exitBtn = (Button) findViewById(R.id.quit);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(student.this, MainActivity.class));
            }
        });

        mapBtn = (FloatingActionButton) findViewById(R.id.studentFilter);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMap = new Intent(student.this, eventMap.class);
                toMap.putExtra("class", "student");
                toMap.putExtra("category", category);
                toMap.putExtra("subcategory", subcategory);
                startActivity(toMap);
            }
        });

        filter = (FloatingActionButton) findViewById(R.id.filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(student.this, filterSettings.class).putExtra("class", "student"));
            }
        });

        myEventBtn = (Button) findViewById(R.id.MyEventsBut);
        myEventBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(student.this, MyEvents.class));
            }
        }));

        createEventBtn = (FloatingActionButton) findViewById(R.id.studentCreate);
        createEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(student.this, createEvent.class).putExtra("class", "student"));
            }
        });
        mirajDatabase = FirebaseDatabase.getInstance("https://campusdiscovery-d2e9f-default-rtdb.firebaseio.com/").getReference("Events");
        final int[] currPage = new int[1];
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Page> pages = new ArrayList<>();
                ArrayList<DataSnapshot> childData = new ArrayList<>();
                currPage[0] = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    switch (category) {
                        case "Default":
                            childData.add(child);
                        case "Location":
                            if (subcategory.equals(child.child("location").getValue(String.class).toString().trim())) {
                                childData.add(child);
                            }
                        case "Date":
                            if (child.child("date").getValue(String.class) != null && subcategory.equals(child.child("date").getValue(String.class).trim())) {
                                childData.add(child);
                            }
                        case "Host Type":
                            mirajDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");
                            String userType = mirajDatabase.child(child.child("host").getValue().toString().trim()).child("userType").toString();
                            if (subcategory.equals(userType)) {
                                childData.add(child);
                            }
                    }
                }
                int numPages;
                if (childData.size() % 10 == 0) {
                    numPages = childData.size() / 10;
                } else {
                    numPages = childData.size() / 10 + 1;
                }
                int currChild = 0;
                int pageNum = 1;
                if (numPages == 0) {
                    pages.add(new Page(childData, pageNum));
                }
                for (int i = 0; i < numPages; i++) {
                    if (i == numPages - 1) {
                        pages.add(new Page(childData.subList(currChild, childData.size()), pageNum));
                        break;
                    } else {
                        pages.add(new Page(childData.subList(currChild, currChild + 10), pageNum));
                        currChild += 10;
                        pageNum += 1;
                    }
                }

                RecyclerView recyclerView = findViewById(R.id.recycleviewEvents);
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(pages.get(0).getData(), context, student.this, false);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                Toast.makeText(student.this, "Events successfully loaded.", Toast.LENGTH_SHORT).show();

                nextBtn = (Button) findViewById(R.id.nextPageStud);
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currPage[0] + 1 == pages.size()) {
                            Toast.makeText(student.this, "No next page.", Toast.LENGTH_SHORT).show();
                        } else {
                            currPage[0] += 1;
                            RecyclerView recyclerView = findViewById(R.id.recycleviewEvents);
                            RecyclerViewAdapter adapter = new RecyclerViewAdapter(pages.get(currPage[0]).getData(), context, student.this, false);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(student.this));
                        }
                    }
                });
                backBtn = (Button) findViewById(R.id.backPageStud);
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currPage[0] == 0) {
                            Toast.makeText(student.this, "No previous page.", Toast.LENGTH_SHORT).show();
                        } else {
                            currPage[0] -= 1;
                            RecyclerView recyclerView = findViewById(R.id.recycleviewEvents);
                            RecyclerViewAdapter adapter = new RecyclerViewAdapter(pages.get(currPage[0]).getData(), context, student.this, false);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(student.this));
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(student.this, "Error. Try Again.", Toast.LENGTH_LONG).show();
            }
        };
        mirajDatabase.addValueEventListener(postListener);

    }

    private void exitApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClickEdit(List<DataSnapshot> data, int position) {
        Intent edit = new Intent(this, editEvent.class);
        HashMap<String, Object> attendees = new HashMap<>();
        HashMap<String, Object> maybes = new HashMap<>();
        HashMap<String, Object> wonts = new HashMap<>();
        HashMap<String, Object> enemies = new HashMap<>();
        for (DataSnapshot snapshot : data.get(position).child("attendees").child("Will Attend").getChildren()) {
            attendees.put(snapshot.getKey(), snapshot.getValue());
        }
        for (DataSnapshot snapshot : data.get(position).child("attendees").child("Maybe").getChildren()) {
            maybes.put(snapshot.getKey(), snapshot.getValue());
        }
        for (DataSnapshot snapshot : data.get(position).child("attendees").child("Won't Attend").getChildren()) {
            wonts.put(snapshot.getKey(), snapshot.getValue());
        }
        for (DataSnapshot snapshot : data.get(position).child("attendees").child("I'm praying on ur downfall").getChildren()) {
            enemies.put(snapshot.getKey(), snapshot.getValue());
        }
        edit.putExtra("title", data.get(position).child("title").getValue(String.class));
        edit.putExtra("description", data.get(position).child("eventDescription").getValue(String.class));
        edit.putExtra("location", data.get(position).child("location").getValue(String.class));
        edit.putExtra("date", data.get(position).child("date").getValue(String.class));
        edit.putExtra("startTime", data.get(position).child("startTime").getValue(String.class));
        edit.putExtra("endTime", data.get(position).child("endTime").getValue(String.class));

        edit.putExtra("attendees", attendees);
        edit.putExtra("maybes", maybes);
        edit.putExtra("wonts", wonts);
        edit.putExtra("enemies", enemies);

        edit.putExtra("host", data.get(position).child("host").getValue(String.class));
        edit.putExtra("class", "student");
        edit.putExtra("capacity",data.get(position).child("capacity").getValue(Integer.class));
        edit.putExtra("inviteOnly", data.get(position).child("inviteOnly").getValue(Boolean.class));
        startActivity(edit);
    }

    @Override
    public void onItemClick(List<DataSnapshot> data, int position) {
        String theEmail = currUser.getEmail();
        String emailHead = theEmail.substring(0, theEmail.length()-4);
        System.out.println(emailHead);
        System.out.println(data.get(position).child("invitees").child(emailHead).getValue());
        if ((data.get(position).child("inviteOnly").getValue(Boolean.class) != null && !data.get(position).child("inviteOnly").getValue(Boolean.class))
                || (data.get(position).child("invitees").child(emailHead) != null
                && data.get(position).child("invitees").child(emailHead).getValue() != null
                && data.get(position).child("invitees").child(emailHead).getValue(String.class).equals(theEmail))) {
            Intent rsvp = new Intent(this, rsvpEvent.class);

            rsvp.putExtra("title", data.get(position).child("title").getValue(String.class));
            rsvp.putExtra("description", data.get(position).child("eventDescription").getValue(String.class));
            rsvp.putExtra("location", data.get(position).child("location").getValue(String.class));
            rsvp.putExtra("date", data.get(position).child("date").getValue(String.class));
            rsvp.putExtra("startTime", data.get(position).child("startTime").getValue(String.class));
            rsvp.putExtra("endTime", data.get(position).child("endTime").getValue(String.class));
            rsvp.putExtra("host", data.get(position).child("host").getValue(String.class));
            rsvp.putExtra("attendees", data.get(position).child("attendees").child("Will Attend").getChildrenCount());
            rsvp.putExtra("class", "student");
            rsvp.putExtra("capacity", data.get(position).child("capacity").getValue(Integer.class));
            rsvp.putExtra("inviteOnly", data.get(position).child("inviteOnly").getValue(Boolean.class));
            startActivity(rsvp);
        } else {
            Toast.makeText(student.this, "Not invited to event.", Toast.LENGTH_LONG).show();
        }
    }
}