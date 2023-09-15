package com.example.loginstart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class filterSettings extends AppCompatActivity {
    //GOING TO SORT BY USERTYPE, LOCATION, AND DATE
    private Spinner subCat;
    private ArrayAdapter<String> subCatAdapter;
    private DatabaseReference mirajDatabase;
    private String[] locations;
    private String[] userType;
    private ArrayList<String> dates;
    private RadioGroup category;
    private ArrayList<String> returnList;
    private Context context;
    private Button done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_settings);
        context = this;
        category = (RadioGroup) findViewById(R.id.filterGroup);
        subCat = (Spinner) findViewById(R.id.filterSpinner);
        locations = new String[]{"West Dorms", "CRC", "CRC Field", "Student Center",
                "Tech Green", "CULC", "Klaus", "CoC", "East Dorms", "NAve", "Bobby Dodd Stadium", "McCamish Pavilion", "Tech Square"};
        userType = new String[]{"Student", "Teacher"};
        dates = new ArrayList<>();
        mirajDatabase = FirebaseDatabase.getInstance("https://campusdiscovery-d2e9f-default-rtdb.firebaseio.com/").getReference("Events");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<DataSnapshot> childData = new ArrayList<>();
                String date = null;
                for (DataSnapshot child : snapshot.getChildren()) {
                    if(child.child("date").getValue(String.class) != null) {
                        date = child.child("date").getValue(String.class).toString().trim();
                    }
                    if (date != null && !dates.contains(date)) {
                        dates.add(date);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(filterSettings.this, "Error loading dates.", Toast.LENGTH_LONG).show();
            }
        };
        category.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (category.getCheckedRadioButtonId() != -1) {
                    System.out.println("reached");
                    RadioButton selected= (RadioButton) findViewById(category.getCheckedRadioButtonId());
                    TextView subCatHeader = (TextView) findViewById(R.id.filterSubcat);
                    switch (selected.getText().toString().trim()) {
                        case "Default":
                            subCatHeader.setVisibility(View.INVISIBLE);
                            subCat.setVisibility(View.INVISIBLE);
                            break;
                        case "Location":
                            subCatAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, locations);
                            subCat.setAdapter(subCatAdapter);
                            subCatHeader.setVisibility(View.VISIBLE);
                            subCat.setVisibility(View.VISIBLE);
                            break;
                        case "Date":
                            subCatAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, dates);
                            subCat.setAdapter(subCatAdapter);
                            subCatHeader.setVisibility(View.VISIBLE);
                            subCat.setVisibility(View.VISIBLE);
                            break;
                        case "Host Type":
                            subCatAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, userType);
                            subCat.setAdapter(subCatAdapter);
                            subCatHeader.setVisibility(View.VISIBLE);
                            subCat.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        });

        done = (Button) findViewById(R.id.doneBtn);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton selected= (RadioButton) findViewById(category.getCheckedRadioButtonId());
                String returnActivity = getIntent().getStringExtra("class");
                Intent returning = new Intent(filterSettings.this, student.class);
                if (returnActivity.equals("eventMap")) {
                    returning = new Intent(filterSettings.this, eventMap.class);
                }
                if (category.getCheckedRadioButtonId() == -1 || selected.getText().toString().trim().equals("Default")) {
                    returning.putExtra("category", "Default");
                    returning.putExtra("subcategory", "Default");
                } else {
                    returning.putExtra("category", selected.getText().toString().trim());
                    returning.putExtra("subcategory", subCat.getSelectedItem().toString());
                }
                returning.putExtra("class", "filterSettings");
                startActivity(returning);
            }
        });
        mirajDatabase.addValueEventListener(postListener);
    }
}