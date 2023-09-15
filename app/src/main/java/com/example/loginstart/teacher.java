package com.example.loginstart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class teacher extends AppCompatActivity implements RecyclerViewInterface {
    private Button exitBtn;
    private FloatingActionButton createEventBtn;
    private DatabaseReference mirajDatabase;
    private Button nextBtn;
    private Button backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        exitBtn = (Button) findViewById(R.id.quit);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(teacher.this, MainActivity.class));
            }
        });

        createEventBtn = (FloatingActionButton) findViewById(R.id.facultyCreate);
        createEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(teacher.this, createEvent.class).putExtra("class", "teacher"));
            }
        });
        mirajDatabase = FirebaseDatabase.getInstance("https://campusdiscovery-d2e9f-default-rtdb.firebaseio.com/").getReference("Events");
        final int[] currPage = new int[1];
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Page> pages = new ArrayList<>();
                ArrayList<DataSnapshot> childData= new ArrayList<>();
                currPage[0] = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    childData.add(child);
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

                RecyclerView recyclerView = findViewById(R.id.recycleviewfaculty);
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(pages.get(0).getData(), context, teacher.this, false);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                Toast.makeText(teacher.this, "Events successfully loaded.", Toast.LENGTH_LONG).show();

                nextBtn = (Button) findViewById(R.id.nextPageFact);
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currPage[0] + 1 == pages.size()) {
                            Toast.makeText(teacher.this, "No next page.", Toast.LENGTH_LONG).show();
                        } else {
                            currPage[0] += 1;
                            RecyclerView recyclerView = findViewById(R.id.recycleviewfaculty);
                            RecyclerViewAdapter adapter = new RecyclerViewAdapter(pages.get(currPage[0]).getData(), context, teacher.this, false);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(teacher.this));
                        }
                    }
                });
                backBtn = (Button) findViewById(R.id.backPageFact);
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currPage[0] == 0) {
                            Toast.makeText(teacher.this, "No previous page.", Toast.LENGTH_LONG).show();
                        } else {
                            currPage[0] -= 1;
                            RecyclerView recyclerView = findViewById(R.id.recycleviewfaculty);
                            RecyclerViewAdapter adapter = new RecyclerViewAdapter(pages.get(currPage[0]).getData(), context, teacher.this, false);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(teacher.this));
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(teacher.this, "Error. Try Again.", Toast.LENGTH_LONG).show();
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
        edit.putExtra("title", data.get(position).child("title").getValue(String.class));
        edit.putExtra("description", data.get(position).child("eventDescription").getValue(String.class));
        edit.putExtra("location", data.get(position).child("location").getValue(String.class));
        edit.putExtra("time", data.get(position).child("time").getValue(String.class));
        edit.putExtra("host", data.get(position).child("host").getValue(String.class));
        edit.putExtra("class", "teacher");
        startActivity(edit);
    }

    @Override
    public void onItemClick(List<DataSnapshot> data, int position) {
        Intent rsvp = new Intent(this, rsvpEvent.class);

        rsvp.putExtra("title", data.get(position).child("title").getValue(String.class));
        rsvp.putExtra("description", data.get(position).child("eventDescription").getValue(String.class));
        rsvp.putExtra("location", data.get(position).child("location").getValue(String.class));
        rsvp.putExtra("time", data.get(position).child("time").getValue(String.class));
        rsvp.putExtra("host", data.get(position).child("host").getValue(String.class));
        rsvp.putExtra("class", "student");
        startActivity(rsvp);
    }
}