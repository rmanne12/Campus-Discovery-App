package com.example.loginstart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewAdapter2.MyViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    List<DataSnapshot> data;
    Context context;
    DatabaseReference eventDatabase = FirebaseDatabase.getInstance("https://campusdiscovery-d2e9f-default-rtdb.firebaseio.com/").getReference("Events");
    userInfo eventHost;
    static String host;
    static String title;
    FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    public RecyclerViewAdapter2(String title, String host, List<DataSnapshot> data, Context context, RecyclerViewInterface recyclerViewInterface) {
        RecyclerViewAdapter2.title = title;
        RecyclerViewAdapter2.host = host;
        this.data = data;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }
    @NonNull
    @Override
    public RecyclerViewAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.attendee_cell, parent, false);

        return new RecyclerViewAdapter2.MyViewHolder(view, recyclerViewInterface, data);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter2.MyViewHolder holder, int position) {
        holder.name.setText(data.get(position).getValue(String.class));
        holder.removeUser = data.get(position).getKey();

        if (!(host.equals(currUser.getUid()))) {
            holder.removeAttendee.setVisibility(View.GONE);
        } else {
            holder.removeAttendee.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView name;
        String removeUser;
        String editorId;
        String userType;
        FirebaseUser currUser;
        Button removeAttendee;
        Button editEvent;
        List<DataSnapshot> data;
        DatabaseReference eventDatabase = FirebaseDatabase.getInstance("https://campusdiscovery-d2e9f-default-rtdb.firebaseio.com/").getReference("Events");

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface, List<DataSnapshot> data) {
            super(itemView);

            name = itemView.findViewById(R.id.attendeeName);


            currUser = FirebaseAuth.getInstance().getCurrentUser();



            // Remove Attendee Button
            removeAttendee = itemView.findViewById(R.id.removeAttendee);

            // Remove functionality
            removeAttendee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (String status : rsvpEvent.rsvpStatuses) {
                        eventDatabase.child(title).child("attendees").child(status).child(removeUser).removeValue();
                    }
                }
            });
        }
    }
}

