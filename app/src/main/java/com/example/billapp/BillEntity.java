package com.example.billapp;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "bills")
public class BillEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private double amount;
    private String type;
    private String category;
    private String note;
    private Date billDate;

    public BillEntity() {
    }

    @Ignore
    public BillEntity(double amount, String type, String category, String note, Date billDate) {
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.note = note;
        this.billDate = billDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }
}