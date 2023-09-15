package com.example.loginstart;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class createEvent extends AppCompatActivity {
    private EditText title;
    private EditText eventDescription;
    private Spinner location;
    private EditText time;
    private Button startTime;
    private Button endTime;
    private Button date;
    private Calendar calStartTime = Calendar.getInstance();
    private Calendar calEndTime = Calendar.getInstance();
    private EditText capacity;
    private TextView invitePpl;
    private Switch inviteMode;
    private int startHour, startMinute, endHour, endMinute;
    private String txtTitle, txtEventDescription, txtLocation, txtStartTime, txtEndTime, txtDate;
    private Button createBtn, yesBtn, laterBtn;
    private LinearLayout invBtns;
    private int eventCap;
    private boolean onlyInv;
    private Button returnToDashBtn;
    private Event created;
    private FirebaseUser currUser;
    private DatabaseReference mirajDatabase;
    private userInfo currUserInfo;
    String[] locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        title = (EditText) findViewById(R.id.title);
        eventDescription = (EditText) findViewById(R.id.eventDescription);
        location = (Spinner) findViewById(R.id.location);
        locations = new String[]{"West Dorms", "CRC", "CRC Field", "Student Center",
        "Tech Green", "CULC", "Klaus", "CoC", "East Dorms", "NAve", "Bobby Dodd Stadium", "McCamish Pavilion", "Tech Square"};
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locations);
        location.setAdapter(locationAdapter);
        startTime = (Button) findViewById(R.id.startTime);
        endTime = (Button) findViewById(R.id.endTime);
        date = (Button) findViewById(R.id.date);
        capacity = (EditText) findViewById(R.id.capacity);
        inviteMode = (Switch) findViewById(R.id.invite);
        invBtns = (LinearLayout) findViewById(R.id.invButtons);
        yesBtn = (Button) findViewById(R.id.addInvBtn);
        laterBtn = (Button) findViewById(R.id.laterBtn);
        invitePpl = (TextView) findViewById(R.id.invPplText);

        calStartTime.set(Calendar.HOUR_OF_DAY, 0);
        calStartTime.set(Calendar.MINUTE, 0);
        calEndTime.set(Calendar.HOUR_OF_DAY, 24);

        returnToDashBtn = (Button) findViewById(R.id.returnToDash);
        returnToDashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caller = getIntent().getStringExtra("class");
                doReturn(caller);
            }
        });


        createBtn = (Button) findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Create registered");
                txtTitle = title.getText().toString().trim();
                txtEventDescription = eventDescription.getText().toString().trim();
                txtLocation = location.getSelectedItem().toString().trim();
                txtStartTime = startTime.getText().toString();
                txtEndTime = endTime.getText().toString();
                txtDate = date.getText().toString();

                eventCap = (int) Integer.valueOf(capacity.getText().toString().trim());
                onlyInv = inviteMode.isChecked();
                currUser = FirebaseAuth.getInstance().getCurrentUser();
                mirajDatabase = FirebaseDatabase.getInstance("https://campusdiscovery-d2e9f-default-rtdb.firebaseio.com/").getReference("UserInfo");
                mirajDatabase.child(currUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userInfo currUserInfo = snapshot.getValue(userInfo.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(createEvent.this, "ERROR", Toast.LENGTH_LONG).show();
                    }
                });
                created = new Event(txtTitle, currUser.getUid(), txtEventDescription, txtLocation, txtDate, txtStartTime, txtEndTime, eventCap, onlyInv);
                mirajDatabase = FirebaseDatabase.getInstance("https://campusdiscovery-d2e9f-default-rtdb.firebaseio.com/").getReference("Events");
                mirajDatabase.child(txtTitle).setValue(created).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(createEvent.this, "Successfully created.", Toast.LENGTH_LONG).show();
                            String caller = getIntent().getStringExtra("class");
                            doReturn(caller);
                        } else {
                            Toast.makeText(createEvent.this, "Error. Try Again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

    }

    private void exitApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void doReturn(String caller) {
        if (inviteMode.isChecked() && invitePpl.getVisibility() != View.VISIBLE) {
            String calling = caller;
            invitePpl.setVisibility(View.VISIBLE);
            invBtns.setVisibility(View.VISIBLE);
            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent goInvite = new Intent(createEvent.this, addInvitees.class);
                    goInvite.putExtra("title", txtTitle);
                    goInvite.putExtra("userType", caller);
                    startActivity(goInvite);
                }
            });
            laterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inviteMode.setChecked(false);
                    String caller = getIntent().getStringExtra("class");
                    doReturn(caller);
                }
            });
        } else {
            switch (caller) {
                case "student":
                    startActivity(new Intent(createEvent.this, student.class));
                    break;
                case "teacher":
                    startActivity(new Intent(createEvent.this, student.class));
                    break;
            }
        }
    }

    public void pickStartTime(View view) {

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String am_pm = "";

                Calendar temp = Calendar.getInstance();
                temp.set(Calendar.HOUR_OF_DAY, hour);
                temp.set(Calendar.MINUTE, minute);
                temp.set(Calendar.MILLISECOND, 0);

                System.out.println(temp.getTime());
                System.out.println(calEndTime.getTime());

                if (temp.after(calEndTime) || isEqual(temp, calEndTime)) {
                    Toast.makeText(createEvent.this, "Start time should be before End time", Toast.LENGTH_SHORT).show();
                } else {
                    Calendar datetime = Calendar.getInstance();
                    datetime.set(Calendar.HOUR_OF_DAY, hour);
                    datetime.set(Calendar.MINUTE, minute);
                    datetime.set(Calendar.MILLISECOND, 0);

                    calStartTime = datetime;

                    if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                        am_pm = "AM";
                    else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                        am_pm = "PM";

                    startHour = (datetime.get(Calendar.HOUR) == 0) ? 12 : datetime.get(Calendar.HOUR);
                    startMinute = datetime.get(Calendar.MINUTE);
                    startTime.setText(String.format(Locale.getDefault(), "%02d:%02d%s", startHour, startMinute, am_pm));
                };
            }
        };
        int style = AlertDialog.THEME_HOLO_LIGHT;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener, startHour, startMinute, false);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    public void pickEndTime(View view) {

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String am_pm = "";

                Calendar temp = Calendar.getInstance();
                temp.set(Calendar.HOUR_OF_DAY, hour);
                temp.set(Calendar.MINUTE, minute);
                temp.set(Calendar.MILLISECOND, 0);

                System.out.println(temp.getTime());
                System.out.println(calStartTime.getTime());

                if (temp.get(Calendar.HOUR) == 0 && temp.get(Calendar.AM_PM) == Calendar.AM) {
                    Toast.makeText(createEvent.this, "Earliest End time is 1:00AM", Toast.LENGTH_SHORT).show();
                } else if (temp.before(calStartTime) || isEqual(temp, calStartTime)) {
                    Toast.makeText(createEvent.this, "End time should be after Start time", Toast.LENGTH_SHORT).show();
                } else {
                    Calendar datetime = Calendar.getInstance();
                    datetime.set(Calendar.HOUR_OF_DAY, hour);
                    datetime.set(Calendar.MINUTE, minute);
                    datetime.set(Calendar.MILLISECOND, 0);

                    calEndTime = datetime;

                    if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                        am_pm = "AM";
                    else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                        am_pm = "PM";

                    endHour = (datetime.get(Calendar.HOUR) == 0) ? 12 : datetime.get(Calendar.HOUR);
                    endMinute = datetime.get(Calendar.MINUTE);
                    endTime.setText(String.format(Locale.getDefault(), "%02d:%02d%s", endHour, endMinute, am_pm));
                }
            }
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener, endHour, endMinute, false);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    public void pickDate(View view) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                Calendar temp = Calendar.getInstance();
                temp.set(Calendar.YEAR, year);
                temp.set(Calendar.MONTH, month);
                temp.set(Calendar.DAY_OF_MONTH, day);

                Calendar today = Calendar.getInstance();

                if (temp.before(today)) {
                    Toast.makeText(createEvent.this, "Cannot create Past Events", Toast.LENGTH_SHORT).show();
                } else {
                    month += 1;
                    String dateString = toDateString(day, month, year);
                    date.setText(dateString);
                }
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private String toDateString(int day, int month, int year) {
        String date = "";

        switch (month) {
            case 1:
                date += "JAN";
                break;
            case 2:
                date += "FEB";
                break;
            case 3:
                date += "MAR";
                break;
            case 4:
                date += "APR";
                break;
            case 5:
                date += "MAY";
                break;
            case 6:
                date += "JUN";
                break;
            case 7:
                date += "JUL";
                break;
            case 8:
                date += "AUG";
                break;
            case 9:
                date += "SEP";
                break;
            case 10:
                date += "OCT";
                break;
            case 11:
                date += "NOV";
                break;
            case 12:
                date += "DEC";
        }

        date += " " + day + " " + year;

        return date;
    }

    private boolean isEqual(Calendar first, Calendar second) {
        if (first.get(Calendar.AM_PM) == second.get(Calendar.AM_PM)) {
            if (first.get(Calendar.HOUR) == second.get(Calendar.HOUR)) {
                if (first.get(Calendar.MINUTE) == second.get(Calendar.MINUTE)) {
                    return true;
                }
            }
        }
        return false;
    }
}
