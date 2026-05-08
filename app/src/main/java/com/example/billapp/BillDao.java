package com.example.billapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BillDao {
    @Query("SELECT * FROM bills")
    List<BillEntity> getAllBills();

    @Query("SELECT * FROM bills WHERE id = :id")
    BillEntity getBillById(int id);

    @Insert
    long insertBill(BillEntity bill);

    @Update
    int updateBill(BillEntity bill);

    @Delete
    int deleteBill(BillEntity bill);

    @Query("DELETE FROM bills WHERE id = :id")
    int deleteBillById(int id);
}