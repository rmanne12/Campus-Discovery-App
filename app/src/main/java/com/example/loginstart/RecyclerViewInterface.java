package com.example.loginstart;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

public interface RecyclerViewInterface {
    void onClickEdit(List<DataSnapshot> data, int position);
    void onItemClick(List<DataSnapshot> data, int position);
}
