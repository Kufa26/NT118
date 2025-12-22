package com.example.login_sigup.database.transaction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.login_sigup.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
public class TransactionHandle {

    private SQLiteDatabase db;

    public TransactionHandle(Context context) {
        db = new DatabaseHelper(context).getWritableDatabase();
    }

    public long insert(Transaction t) {
        ContentValues values = new ContentValues();
        values.put("idUser", t.getIdUser());
        values.put("idCategory", t.getIdCategory());
        values.put("amount", t.getAmount());
        values.put("note", t.getNote());
        values.put("date", t.getDate());
        values.put("typeTransaction", t.getTypeTransaction());

        return db.insert("TransactionTable", null, values);
    }

    public List<Transaction> getAll() {
        List<Transaction> list = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM TransactionTable ORDER BY date DESC", null);

        while (c.moveToNext()) {
            list.add(new Transaction(
                    c.getLong(0),
                    c.getLong(1),
                    c.getLong(2),
                    c.getDouble(3),
                    c.getString(4),
                    c.getString(5),
                    c.getString(6)
            ));
        }
        c.close();
        return list;
    }
}
