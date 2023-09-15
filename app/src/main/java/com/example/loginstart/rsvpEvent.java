package com.example.loginstart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class rsvpEvent extends AppCompatActivity{
    TextView title;
    TextView location;
    TextView time;
    TextView date;
    TextView description;
    TextView host;
    TextView attendees;
    DatabaseReference userDatabase;
    Button returnToDashBtn;
    Button RSVPBtn;
    FirebaseUser currUser;
    TextView viewAttendees;
    TextView capString;
    static String[] rsvpStatuses = {"Will Attend", "Maybe", "Won't Attend", "I'm praying on ur downfall"};


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsvp_event);

        Spinner dropdown = findViewById(R.id.RSVPdropdown);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, rsvpStatuses);
        dropdown.setAdapter(adapter);

        final String[] rsvpStatus = new String[1];
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                rsvpStatus[0] = adapterView.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        title = (TextView) findViewById(R.id.titleRSVP);
        location = (TextView) findViewById(R.id.locationRSVP);
        time = (TextView) findViewById(R.id.timeRSVP);
        date = (TextView) findViewById(R.id.dateRSVP);
        description = (TextView) findViewById(R.id.descriptionRSVP);
        host = (TextView) findViewById(R.id.hostRSVP);
        attendees = (TextView) findViewById(R.id.numAttendees);
        viewAttendees = (TextView) findViewById(R.id.viewAttendees);
        capString = (TextView) findViewById(R.id.capString);

        String titleString = getIntent().getStringExtra("title");
        String descriptionString = getIntent().getStringExtra("description");
        String locationString = getIntent().getStringExtra("location");
        String startTimeString = getIntent().getStringExtra("startTime");
        String endTimeString = getIntent().getStringExtra("endTime");
        String timeString = startTimeString + " - " + endTimeString;
        String dateString = getIntent().getStringExtra("date");
        String hostString = getIntent().getStringExtra("host");
        String attendeesString = "" + getIntent().getLongExtra("attendees", 0);

        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");
        userDatabase.child(hostString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo eventHost = snapshot.getValue(userInfo.class);
                if (eventHost !=null) {
                    host.setText(eventHost.getFullName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Failed");
            }
        });

        title.setText(titleString);
        location.setText(locationString);
        time.setText(timeString);
        date.setText(dateString);
        description.setText(descriptionString);
        attendees.setText(attendeesString);
        capString.setText(String.valueOf(getIntent().getIntExtra("capacity", 10)));

        final String[] username = new String[1];

        currUser = FirebaseAuth.getInstance().getCurrentUser();
        userDatabase.child(currUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo user = snapshot.getValue(userInfo.class);
                username[0] = user.getFullName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Failed to get username");
            }
        });

        viewAttendees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(rsvpEvent.this, showAttendees.class);
                intent.putExtra("title", titleString);
                intent.putExtra("host", hostString);
                startActivity(intent);
            }
        });

        DatabaseReference eventDatabase = FirebaseDatabase.getInstance().getReference("Events");

        RSVPBtn = (Button) findViewById(R.id.RSVP);
        RSVPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if((long) getIntent().getIntExtra("capacity", 0) < (long) getIntent().getLongExtra("attendees", 0)) { //FUTURE FUNCTIONALITY
                    for (String status : rsvpStatuses) {
                        eventDatabase.child(titleString).child("attendees").child(status).child(currUser.getUid()).removeValue();
                    }
                    eventDatabase.child(titleString).child("attendees").child(rsvpStatus[0]).child(currUser.getUid()).setValue(username[0]);
                    if (rsvpStatus[0].equals("Will Attend")) {
                        userDatabase.child(currUser.getUid()).child("events").child(titleString).setValue(titleString);
                    } else {
                        userDatabase.child(currUser.getUid()).child("events").child(titleString).removeValue();
                    }
                    Toast.makeText(rsvpEvent.this, "Successfully changed RSVP status", Toast.LENGTH_SHORT).show();
            }
        });


        returnToDashBtn = (Button) findViewById(R.id.RSVPreturn);
        returnToDashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caller = getIntent().getStringExtra("class");
                doReturn(caller);
            }
        });
    }
    private void exitApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void doReturn(String caller) {
        switch (caller) {
            case "MyEvents":
                startActivity(new Intent(rsvpEvent.this, MyEvents.class));
                break;
            case "student":
                startActivity(new Intent(rsvpEvent.this, student.class));
                break;
            case "teacher":
                startActivity(new Intent(rsvpEvent.this, student.class));
                break;
        }
    }
}
