package com.example.loginstart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    List<DataSnapshot> data;
    Boolean MyEvent;
    Context context;
    DatabaseReference userDatabase;
    DatabaseReference eventDatabase;
    userInfo eventHost;
    public RecyclerViewAdapter(List<DataSnapshot> data, Context context, RecyclerViewInterface recyclerViewInterface, boolean MyEvent) {
        this.data = data;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
        this.MyEvent = MyEvent;
    }
    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.event_cell, parent, false);

        return new RecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface, data);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(data.get(position).child("title").getValue(String.class));
        holder.date.setText(data.get(position).child("date").getValue(String.class));
        holder.location.setText(data.get(position).child("location").getValue(String.class));
        holder.description.setText(data.get(position).child("eventDescription").getValue(String.class));
        holder.editorId = data.get(position).child("host").getValue(String.class);
        userDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");
        userDatabase.child(data.get(position).child("host").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventHost = snapshot.getValue(userInfo.class);
                if (eventHost !=null) {
                    holder.host.setText(eventHost.getFullName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Failed");
            }
        });
        /*ValueEventListener recycleListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventHost = snapshot.getValue(userInfo.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Failed");
            }
        };
        userDatabase.child(data.get(position).child("host").getValue(String.class)).addValueEventListener(recycleListener);
        if (eventHost !=null) {
            holder.host.setText(eventHost.getFullName());
            holder.userType = eventHost.getUserType();
        }
        if (!(holder.editorId.equals(holder.currUser.getUid()))) {
            holder.removeEvent.setVisibility(View.GONE);
            holder.editEvent.setVisibility(View.GONE);
        }
        if (holder.userType != null && holder.userType.equals("Admin")) {
            holder.removeEvent.setVisibility(View.VISIBLE);
        }*/
        userDatabase.child(holder.currUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo temp = snapshot.getValue(userInfo.class);
                if (temp != null) {
                    holder.userType = temp.getUserType();
                }
                userDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        System.out.println("Host of event: " + holder.host.getText().toString());
                        System.out.println("Curr user: " + holder.currUser.getUid());
                        /*if (!(holder.editorId.equals(holder.currUser.getUid()))) {
                            holder.removeEvent.setVisibility(View.GONE);
                            holder.editEvent.setVisibility(View.GONE);
                        }*/
                        if (holder.userType != null && holder.userType.equals("Admin")) {
                            holder.removeEvent.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("Failed");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (!(holder.editorId.equals(holder.currUser.getUid()))) {
            holder.removeEvent.setVisibility(View.GONE);
            holder.editEvent.setVisibility(View.GONE);
        } else {
            holder.removeEvent.setVisibility(View.VISIBLE);
            holder.editEvent.setVisibility(View.VISIBLE);
        }
        if (MyEvent) {
            eventDatabase = FirebaseDatabase.getInstance("https://campusdiscovery-d2e9f-default-rtdb.firebaseio.com/").getReference("Events");
            FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference userEvents = userDatabase.child(currUser.getUid()).child("events");
            userEvents.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Set<String> eventData = new HashSet<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        eventData.add(child.getValue(String.class));
                    }
                    System.out.println("AllEvents: " + eventData);
                    ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ArrayList<DataSnapshot> childData = new ArrayList<>();
                            for (DataSnapshot child : snapshot.getChildren()) {
                                if (eventData.contains(child.child("title").getValue(String.class))) {
                                    childData.add(child);
                                }
                            }
                            System.out.println("MyEvents: " + childData);
                            Calendar start1 = militaryTimeConverter(data.get(position).child("startTime").getValue(String.class));
                            Calendar end1 = militaryTimeConverter(data.get(position).child("endTime").getValue(String.class));
                            String date1 = data.get(position).child("date").getValue(String.class);
                            for (DataSnapshot event : childData) {
                                Calendar start2 = militaryTimeConverter(event.child("startTime").getValue(String.class));
                                Calendar end2 = militaryTimeConverter(event.child("endTime").getValue(String.class));
                                String date2 = event.child("date").getValue(String.class);
                                System.out.println("start: " + start2);
                                System.out.println("end: " + end2);
                                System.out.println("Conflict: " + isConflct(start1, end1, start2, end2));
                                if (isConflct(start1, end1, start2, end2) && date1.equals(date2)) {
                                    holder.alertBtn.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("Failed");
                        }
                    };
                    eventDatabase.addValueEventListener(postListener);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("Failed");
                }
            });
        }
    }
    private static Calendar militaryTimeConverter(String standardTime) {
        String hr = standardTime.substring(0, 2);
        String min = standardTime.substring(3, 5);
        String am_pm = standardTime.substring(5, 7);

        int hour = Integer.parseInt(hr);
        int minutes = Integer.parseInt(min);


        if (hour == 12 && am_pm.equals("AM")) {
            hour = 0;
        } else if (hour != 12 && am_pm.equals("PM")) {
            hour += 12;
        }

        String strMin = "";
        String strHr = "";


        if (hour < 10) {
            strHr = "0" + hour;
        } else {
            strHr = "" + hour;
        }

        if (minutes < 10) {
            strMin = "0" + minutes;
        } else {
            strMin = "" + minutes;
        }

        String finalResult = strHr + ":" + strMin + ":" + "00";

        Calendar militaryTime = Calendar.getInstance();
        try {
            Date timeFormat = new SimpleDateFormat("HH:mm:ss").parse(finalResult);
            militaryTime.setTime(timeFormat);
            return militaryTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return militaryTime;
    }
    private static boolean isConflct(Calendar newEventStartTime, Calendar newEventEndTime, Calendar oldEventStartTime, Calendar oldEventEndTime) {

        Date nest = newEventStartTime.getTime();
        Date neet = newEventEndTime.getTime();
        Date oest = oldEventStartTime.getTime();
        Date oeet = oldEventEndTime.getTime();


        if (nest.after(oest) && nest.before(oeet)) {
            return true;
        }

        if (neet.after(oest) && neet.before(oeet)) {
            return true;
        }

        if (nest.before(oest) && neet.after(oeet)) {
            return true;
        }

        return false;
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView title, date, location, description, host;
        String editorId;
        String userType;
        FirebaseUser currUser;
        Button removeEvent;
        Button editEvent;
        Button alertBtn;
        List<DataSnapshot> data;
        DatabaseReference mirajDatabase = FirebaseDatabase.getInstance("https://campusdiscovery-d2e9f-default-rtdb.firebaseio.com/").getReference("Events");
        DatabaseReference mirajUsers = FirebaseDatabase.getInstance("https://campusdiscovery-d2e9f-default-rtdb.firebaseio.com/").getReference("UserInfo");

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface, List<DataSnapshot> data) {
            super(itemView);

            title = itemView.findViewById(R.id.eventTitle);
            date = itemView.findViewById((R.id.eventDate));
            location = itemView.findViewById(R.id.eventLocation);
            description = itemView.findViewById((R.id.eventDes));
            host = itemView.findViewById(R.id.eventHost);

            currUser = FirebaseAuth.getInstance().getCurrentUser();
            /*
            mirajUsers.child(currUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userInfo temp = snapshot.getValue(userInfo.class);
                    if (temp != null) {
                        userType = temp.getUserType();
                    }
                    mirajDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            System.out.println("Host of event: " + host.getText().toString());
                            System.out.println("Curr user: " + currUser.getUid());
                            if (!(editorId.equals(currUser.getUid()))) {
                                removeEvent.setVisibility(View.GONE);
                                editEvent.setVisibility(View.GONE);
                            }
                            if (userType != null && userType.equals("Admin")) {
                                removeEvent.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("Failed");
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            */
            //Edit Button
            editEvent = itemView.findViewById(R.id.editEvent);

            // Remove Button
            removeEvent = itemView.findViewById(R.id.removeEvent);

            // Conflict button
            alertBtn = itemView.findViewById((R.id.alert));

            //Button Visibility
            /*mirajDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    System.out.println("Host of event: " + host.getText().toString());
                    System.out.println("Curr user: " + currUser.getUid());
                    if (!(editorId.equals(currUser.getUid()))) {
                        removeEvent.setVisibility(View.GONE);
                        editEvent.setVisibility(View.GONE);
                    }
                    if (userType != null && userType.equals("Admin")) {
                        removeEvent.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("Failed");
                }
            }); */

            // Remove functionality
            removeEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mirajDatabase.child(title.getText().toString()).removeValue();
                }
            });

            // Edit functionality
            editEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onClickEdit(data, position);
                        }
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(data, pos);
                        }
                    }
                }
            });
        }
    }
}

