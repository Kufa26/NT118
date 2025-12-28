package com.example.cashmate.database.Category;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cashmate.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CategoryHandle {

    private final DatabaseHelper dbHelper;

    public CategoryHandle(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // ===== INSERT =====
    public long insertCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nameCategory", category.getNameCategory());
        values.put("typeCategory", category.getTypeCategory());
        values.put("iconCategory", category.getIconCategory());

        long id = db.insert("Category", null, values);
        db.close();
        return id;
    }

    // ===== GET BY TYPE =====
    public List<Category> getCategoriesByType(String type) {
        List<Category> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM Category WHERE typeCategory = ?",
                new String[]{type}
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(new Category(
                        cursor.getLong(cursor.getColumnIndexOrThrow("idCategory")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nameCategory")),
                        cursor.getString(cursor.getColumnIndexOrThrow("typeCategory")),
                        cursor.getString(cursor.getColumnIndexOrThrow("iconCategory"))
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    // ===== DELETE (CASCADE DELETE TRANSACTIONS) =====
    public boolean deleteCategory(long idCategory) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // 1️⃣ XÓA TẤT CẢ GIAO DỊCH THUỘC NHÓM NÀY
            db.delete(
                    "TransactionTable",
                    "idCategory = ?",
                    new String[]{String.valueOf(idCategory)}
            );

            // 2️⃣ XÓA NHÓM
            int rows = db.delete(
                    "Category",
                    "idCategory = ?",
                    new String[]{String.valueOf(idCategory)}
            );

            db.setTransactionSuccessful();
            return rows > 0;
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}
