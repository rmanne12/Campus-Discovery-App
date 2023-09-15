package com.example.loginstart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.List;

public class eventMap extends AppCompatActivity implements View.OnClickListener {
    private TextView WestDorms, CRC, CRCField, StudentCenter, TechGreen, CULC, Klaus, CoC, EastDorms, NAve, BobbyDodd, McCamish, TechSquare;
    private ArrayList<DataSnapshot> iWestDorms, iCRC, iCRCField, iStudentCenter, iTechGreen, iCULC, iKlaus, iCoC, iEastDorms, iNAve, iBobbyDodd, iMcCamish, iTechSquare;
    private DatabaseReference mirajDatabase;
    private DatabaseReference mirajDatabaseUser;
    private Context context;
    private String category;
    private String subcategory;
    private FloatingActionButton homeBtn;
    private FloatingActionButton filterBtn;
    private FirebaseUser currUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_map);
        context = this;
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        WestDorms = (TextView) findViewById(R.id.westRes);
        CRC = (TextView) findViewById(R.id.CRC);
        CRCField = (TextView) findViewById(R.id.CRCfield);
        StudentCenter = (TextView) findViewById(R.id.studCenter);
        TechGreen = (TextView) findViewById(R.id.techGreen);
        CULC = (TextView) findViewById(R.id.CULC);
        Klaus = (TextView) findViewById(R.id.Klaus);
        CoC = (TextView) findViewById(R.id.CoC);
        EastDorms = (TextView) findViewById(R.id.EastRes);
        NAve = (TextView) findViewById(R.id.NAve);
        BobbyDodd = (TextView) findViewById(R.id.BobbyDodd);
        McCamish = (TextView) findViewById(R.id.McCamish);
        TechSquare = (TextView) findViewById(R.id.techSquare);

        WestDorms.setOnClickListener(this);
        CRC.setOnClickListener(this);
        CRCField.setOnClickListener(this);
        StudentCenter.setOnClickListener(this);
        TechGreen.setOnClickListener(this);
        CULC.setOnClickListener(this);
        Klaus.setOnClickListener(this);
        CoC.setOnClickListener(this);
        EastDorms.setOnClickListener(this);
        NAve.setOnClickListener(this);
        BobbyDodd.setOnClickListener(this);
        McCamish.setOnClickListener(this);
        TechSquare.setOnClickListener(this);

        iWestDorms = new ArrayList<>();
        iCRC = new ArrayList<>();
        iCRCField = new ArrayList<>();
        iStudentCenter = new ArrayList<>();
        iTechGreen = new ArrayList<>();
        iCULC = new ArrayList<>();
        iKlaus = new ArrayList<>();
        iCoC = new ArrayList<>();
        iEastDorms = new ArrayList<>();
        iNAve = new ArrayList<>();
        iBobbyDodd = new ArrayList<>();
        iMcCamish = new ArrayList<>();
        iTechSquare = new ArrayList<>();

        mirajDatabase = FirebaseDatabase.getInstance("https://campusdiscovery-d2e9f-default-rtdb.firebaseio.com/").getReference("Events");
        mirajDatabaseUser = FirebaseDatabase.getInstance("https://campusdiscovery-d2e9f-default-rtdb.firebaseio.com/").getReference("UserInfo");
        homeBtn = (FloatingActionButton) findViewById(R.id.studentFilter);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (category == null) {
                    category = getIntent().getStringExtra("category").toString().trim();
                    subcategory = getIntent().getStringExtra("subcategory").toString().trim();
                }
                Intent returning = new Intent(eventMap.this, student.class);
                returning.putExtra("category", category);
                returning.putExtra("subcategory", subcategory);
                returning.putExtra("class", "eventMap");
                startActivity(returning);
            }
        });
        filterBtn = (FloatingActionButton) findViewById(R.id.filter);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returning = new Intent(eventMap.this, filterSettings.class);
                returning.putExtra("class", "eventMap");
                startActivity(returning);
            }
        });
        mirajDatabase.addValueEventListener(filterBy(getIntent().getStringExtra("category"), getIntent().getStringExtra("subcategory")));
    }

     public void onClick(View v) {
        Intent viewEvents = new Intent(eventMap.this, student.class);
        TextView theMarker = (TextView) findViewById(v.getId());
        if (Integer.valueOf(theMarker.getText().toString().trim()) != 0) {
            viewEvents.putExtra("category", "Location");
            switch (v.getId()) {
                case R.id.westRes:
                    viewEvents.putExtra("subcategory", "West Dorms");
                    break;
                case R.id.CRC:
                    viewEvents.putExtra("subcategory", "CRC");
                    System.out.println("clicked");
                    break;
                case R.id.CRCfield:
                    viewEvents.putExtra("subcategory", "CRC Field");
                    break;
                case R.id.studCenter:
                    viewEvents.putExtra("subcategory", "Student Center");
                    break;
                case R.id.techGreen:
                    viewEvents.putExtra("subcategory", "Tech Green");
                    break;
                case R.id.CULC:
                    viewEvents.putExtra("subcategory", "CULC");
                    break;
                case R.id.Klaus:
                    viewEvents.putExtra("subcategory", "Klaus");
                    break;
                case R.id.CoC:
                    viewEvents.putExtra("subcategory", "CoC");
                    break;
                case R.id.EastRes:
                    viewEvents.putExtra("subcategory", "East Dorms");
                    break;
                case R.id.NAve:
                    viewEvents.putExtra("subcategory", "NAve");
                    break;
                case R.id.BobbyDodd:
                    viewEvents.putExtra("subcategory", "Bobby Dodd Stadium");
                    break;
                case R.id.McCamish:
                    viewEvents.putExtra("subcategory", "McCamish Pavilion");
                    break;
                case R.id.techSquare:
                    viewEvents.putExtra("subcategory", "Tech Square");
                    break;
            }
            startActivity(viewEvents);
        }
    }

    /* private void filterByLocation(ArrayList<DataSnapshot> dataUsed) {
        private ArrayList<Page> pages;
        final int[] currPage= new int[1];
        private int numPages;
        private int currChild = 0;
        private int pageNum = 1;
        currPage[0] = 0;
        ArrayList<DataSnapshot> childData = dataUsed;
        pages = new ArrayList<>();
        if (childData.size() % 10 == 0) {
            numPages = childData.size() / 10;
        } else {
            numPages = childData.size() / 10 + 1;
        }
        currChild = 0;
        pageNum = 1;
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

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(pages.get(0).getData(), context, eventMap.this);
        mapsRecycler.setAdapter(adapter);
        mapsRecycler.setLayoutManager(new LinearLayoutManager(context));
        Toast.makeText(eventMap.this, "Events successfully loaded.", Toast.LENGTH_SHORT).show();

    }


    public void onClickEdit(List<DataSnapshot> data, int position) {
        return;
    }


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
            rsvp.putExtra("time", data.get(position).child("time").getValue(String.class));
            rsvp.putExtra("host", data.get(position).child("host").getValue(String.class));
            rsvp.putExtra("attendees", data.get(position).child("attendees").child("Will Attend").getChildrenCount());
            rsvp.putExtra("class", "student");
            rsvp.putExtra("capacity", data.get(position).child("capacity").getValue(Integer.class));
            rsvp.putExtra("inviteOnly", data.get(position).child("inviteOnly").getValue(Boolean.class));
            startActivity(rsvp);
        } else {
            Toast.makeText(eventMap.this, "Not invited to event.", Toast.LENGTH_LONG).show();
        }
    } */

    private ValueEventListener filterBy(String category, String subcategory) {
        String category2 = category;
        if (category == null) {
            category2 = "Default";
        }
        iWestDorms.clear(); iCRC.clear(); iCRCField.clear(); iStudentCenter.clear(); iTechGreen.clear();
        iCULC.clear(); iKlaus.clear(); iCoC.clear(); iEastDorms.clear(); iNAve.clear();
        iBobbyDodd.clear(); iMcCamish.clear(); iTechSquare.clear();
        ValueEventListener postListener = null;
        switch(category2) {
            case "Default":
                postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<DataSnapshot> childData = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            switch (child.child("location").getValue(String.class).toString().trim()) {
                                case "West Dorms":
                                    iWestDorms.add(child);
                                    break;
                                case"CRC":
                                    iCRC.add(child);
                                    break;
                                case "CRC Field":
                                    iCRCField.add(child);
                                    break;
                                case "Student Center":
                                    iStudentCenter.add(child);
                                    break;
                                case "Tech Green":
                                    iTechGreen.add(child);
                                    break;
                                case "CULC":
                                    iCULC.add(child);
                                    break;
                                case "Klaus":
                                    iKlaus.add(child);
                                    break;
                                case"CoC":
                                    iCoC.add(child);
                                    break;
                                case "East Dorms":
                                    iEastDorms.add(child);
                                    break;
                                case "NAve":
                                    iNAve.add(child);
                                    break;
                                case "Bobby Dodd Stadium":
                                    iBobbyDodd.add(child);
                                    break;
                                case "McCamish Pavilion":
                                    iMcCamish.add(child);
                                    break;
                                case "Tech Square":
                                    iTechSquare.add(child);
                            }
                            childData.add(child);
                        }
                        if(iWestDorms != null) {
                            WestDorms.setText(String.valueOf(iWestDorms.size()));
                        }
                        if(iCRC != null) {
                            CRC.setText(String.valueOf(iCRC.size()));
                        }
                        if(iCRCField != null) {
                            CRCField.setText(String.valueOf(iCRCField.size()));
                        }
                        if (iStudentCenter != null) {
                            StudentCenter.setText(String.valueOf(iStudentCenter.size()));
                        }
                        if (iTechGreen != null) {
                            TechGreen.setText(String.valueOf(iTechGreen.size()));
                        }
                        if (iCULC != null) {
                            CULC.setText(String.valueOf(iCULC.size()));
                        }
                        if (iKlaus != null) {
                            Klaus.setText(String.valueOf(iKlaus.size()));
                        }
                        if (iCoC != null) {
                            CoC.setText(String.valueOf(iCoC.size()));
                        }
                        if (iEastDorms != null) {
                            EastDorms.setText(String.valueOf(iEastDorms.size()));
                        }
                        if (iNAve != null) {
                            NAve.setText(String.valueOf(iNAve.size()));
                        }
                        if (iBobbyDodd != null) {
                            BobbyDodd.setText(String.valueOf(iBobbyDodd.size()));
                        }
                        if (iMcCamish != null) {
                            McCamish.setText(String.valueOf(iMcCamish.size()));
                        }
                        if (iTechSquare != null) {
                            TechSquare.setText(String.valueOf(iTechSquare.size()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(eventMap.this, "Error loading map.", Toast.LENGTH_LONG).show();
                    }
                };
                break;
            case "Location":
                postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<DataSnapshot> childData = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            if (subcategory.equals(child.child("location").getValue(String.class).toString().trim())) {
                                switch (child.child("location").getValue(String.class).toString().trim()) {
                                    case "West Dorms":
                                        iWestDorms.add(child);
                                        break;
                                    case "CRC":
                                        iCRC.add(child);
                                        break;
                                    case "CRC Field":
                                        iCRCField.add(child);
                                        break;
                                    case "Student Center":
                                        iStudentCenter.add(child);
                                        break;
                                    case "Tech Green":
                                        iTechGreen.add(child);
                                        break;
                                    case "CULC":
                                        iCULC.add(child);
                                        break;
                                    case "Klaus":
                                        iKlaus.add(child);
                                        break;
                                    case "CoC":
                                        iCoC.add(child);
                                        break;
                                    case "East Dorms":
                                        iEastDorms.add(child);
                                        break;
                                    case "NAve":
                                        iNAve.add(child);
                                        break;
                                    case "Bobby Dodd Stadium":
                                        iBobbyDodd.add(child);
                                        break;
                                    case "McCamish Pavilion":
                                        iMcCamish.add(child);
                                        break;
                                    case "Tech Square":
                                        iTechSquare.add(child);
                                }
                                childData.add(child);
                            }
                        }
                        if(iWestDorms != null) {
                            WestDorms.setText(String.valueOf(iWestDorms.size()));
                        }
                        if(iCRC != null) {
                            CRC.setText(String.valueOf(iCRC.size()));
                        }
                        if(iCRCField != null) {
                            CRCField.setText(String.valueOf(iCRCField.size()));
                        }
                        if (iStudentCenter != null) {
                            StudentCenter.setText(String.valueOf(iStudentCenter.size()));
                        }
                        if (iTechGreen != null) {
                            TechGreen.setText(String.valueOf(iTechGreen.size()));
                        }
                        if (iCULC != null) {
                            CULC.setText(String.valueOf(iCULC.size()));
                        }
                        if (iKlaus != null) {
                            Klaus.setText(String.valueOf(iKlaus.size()));
                        }
                        if (iCoC != null) {
                            CoC.setText(String.valueOf(iCoC.size()));
                        }
                        if (iEastDorms != null) {
                            EastDorms.setText(String.valueOf(iEastDorms.size()));
                        }
                        if (iNAve != null) {
                            NAve.setText(String.valueOf(iNAve.size()));
                        }
                        if (iBobbyDodd != null) {
                            BobbyDodd.setText(String.valueOf(iBobbyDodd.size()));
                        }
                        if (iMcCamish != null) {
                            McCamish.setText(String.valueOf(iMcCamish.size()));
                        }
                        if (iTechSquare != null) {
                            TechSquare.setText(String.valueOf(iTechSquare.size()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(eventMap.this, "Error loading map.", Toast.LENGTH_LONG).show();
                    }
                };
                break;
            case "Date":
                postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<DataSnapshot> childData = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            if (child.child("date").getValue(String.class) != null && subcategory.equals(child.child("date").getValue(String.class).toString().trim())) {
                                switch (child.child("location").getValue(String.class).toString().trim()) {
                                    case "West Dorms":
                                        iWestDorms.add(child);
                                        break;
                                    case "CRC":
                                        iCRC.add(child);
                                        break;
                                    case "CRC Field":
                                        iCRCField.add(child);
                                        break;
                                    case "Student Center":
                                        iStudentCenter.add(child);
                                        break;
                                    case "Tech Green":
                                        iTechGreen.add(child);
                                        break;
                                    case "CULC":
                                        iCULC.add(child);
                                        break;
                                    case "Klaus":
                                        iKlaus.add(child);
                                        break;
                                    case "CoC":
                                        iCoC.add(child);
                                        break;
                                    case "East Dorms":
                                        iEastDorms.add(child);
                                        break;
                                    case "NAve":
                                        iNAve.add(child);
                                        break;
                                    case "Bobby Dodd Stadium":
                                        iBobbyDodd.add(child);
                                        break;
                                    case "McCamish Pavilion":
                                        iMcCamish.add(child);
                                        break;
                                    case "Tech Square":
                                        iTechSquare.add(child);
                                }
                                childData.add(child);
                            }
                        }
                        if(iWestDorms != null) {
                            WestDorms.setText(String.valueOf(iWestDorms.size()));
                        }
                        if(iCRC != null) {
                            CRC.setText(String.valueOf(iCRC.size()));
                        }
                        if(iCRCField != null) {
                            CRCField.setText(String.valueOf(iCRCField.size()));
                        }
                        if (iStudentCenter != null) {
                            StudentCenter.setText(String.valueOf(iStudentCenter.size()));
                        }
                        if (iTechGreen != null) {
                            TechGreen.setText(String.valueOf(iTechGreen.size()));
                        }
                        if (iCULC != null) {
                            CULC.setText(String.valueOf(iCULC.size()));
                        }
                        if (iKlaus != null) {
                            Klaus.setText(String.valueOf(iKlaus.size()));
                        }
                        if (iCoC != null) {
                            CoC.setText(String.valueOf(iCoC.size()));
                        }
                        if (iEastDorms != null) {
                            EastDorms.setText(String.valueOf(iEastDorms.size()));
                        }
                        if (iNAve != null) {
                            NAve.setText(String.valueOf(iNAve.size()));
                        }
                        if (iBobbyDodd != null) {
                            BobbyDodd.setText(String.valueOf(iBobbyDodd.size()));
                        }
                        if (iMcCamish != null) {
                            McCamish.setText(String.valueOf(iMcCamish.size()));
                        }
                        if (iTechSquare != null) {
                            TechSquare.setText(String.valueOf(iTechSquare.size()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(eventMap.this, "Error loading map.", Toast.LENGTH_LONG).show();
                    }
                };
                break;
            case "Host Type":
                postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<DataSnapshot> childData = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String userType = mirajDatabaseUser.child(child.child("host").getValue().toString().trim()).child("userType").toString();
                            System.out.println(userType);
                            if (subcategory.equals(userType)) {
                                switch (child.child("location").getValue(String.class).toString().trim()) {
                                    case "West Dorms":
                                        iWestDorms.add(child);
                                        break;
                                    case "CRC":
                                        iCRC.add(child);
                                        break;
                                    case "CRC Field":
                                        iCRCField.add(child);
                                        break;
                                    case "Student Center":
                                        iStudentCenter.add(child);
                                        break;
                                    case "Tech Green":
                                        iTechGreen.add(child);
                                        break;
                                    case "CULC":
                                        iCULC.add(child);
                                        break;
                                    case "Klaus":
                                        iKlaus.add(child);
                                        break;
                                    case "CoC":
                                        iCoC.add(child);
                                        break;
                                    case "East Dorms":
                                        iEastDorms.add(child);
                                        break;
                                    case "NAve":
                                        iNAve.add(child);
                                        break;
                                    case "Bobby Dodd Stadium":
                                        iBobbyDodd.add(child);
                                        break;
                                    case "McCamish Pavilion":
                                        iMcCamish.add(child);
                                        break;
                                    case "Tech Square":
                                        iTechSquare.add(child);
                                }
                                childData.add(child);
                            }
                        }
                        if(iWestDorms != null) {
                            WestDorms.setText(String.valueOf(iWestDorms.size()));
                        }
                        if(iCRC != null) {
                            CRC.setText(String.valueOf(iCRC.size()));
                        }
                        if(iCRCField != null) {
                            CRCField.setText(String.valueOf(iCRCField.size()));
                        }
                        if (iStudentCenter != null) {
                            StudentCenter.setText(String.valueOf(iStudentCenter.size()));
                        }
                        if (iTechGreen != null) {
                            TechGreen.setText(String.valueOf(iTechGreen.size()));
                        }
                        if (iCULC != null) {
                            CULC.setText(String.valueOf(iCULC.size()));
                        }
                        if (iKlaus != null) {
                            Klaus.setText(String.valueOf(iKlaus.size()));
                        }
                        if (iCoC != null) {
                            CoC.setText(String.valueOf(iCoC.size()));
                        }
                        if (iEastDorms != null) {
                            EastDorms.setText(String.valueOf(iEastDorms.size()));
                        }
                        if (iNAve != null) {
                            NAve.setText(String.valueOf(iNAve.size()));
                        }
                        if (iBobbyDodd != null) {
                            BobbyDodd.setText(String.valueOf(iBobbyDodd.size()));
                        }
                        if (iMcCamish != null) {
                            McCamish.setText(String.valueOf(iMcCamish.size()));
                        }
                        if (iTechSquare != null) {
                            TechSquare.setText(String.valueOf(iTechSquare.size()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(eventMap.this, "Error loading map.", Toast.LENGTH_LONG).show();
                    }
                };
                break;
        }
        return postListener;
    }

}