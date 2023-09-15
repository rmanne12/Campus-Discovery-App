package com.example.loginstart;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Page {
    private List<DataSnapshot> data;
    private int pageNum;
    private final int maxSize = 10;
    public Page(List<DataSnapshot> data, int pageNum) {
        this.data = data;
        this.pageNum = pageNum;
    }

    public List<DataSnapshot> getData() {
        return data;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void setData(List<DataSnapshot> data) {
        this.data = data;
    }

}