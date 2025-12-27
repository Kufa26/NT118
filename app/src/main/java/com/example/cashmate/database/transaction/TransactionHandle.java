package com.example.cashmate.database.transaction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cashmate.database.DatabaseHelper;
import java.util.Calendar;


public class TransactionHandle {

    private final DatabaseHelper dbHelper;

    public TransactionHandle(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // ================= INSERT =================
    public long insert(Transaction t) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues v = new ContentValues();
        v.put("idUser", t.getIdUser());
        v.put("idCategory", t.getIdCategory());
        v.put("amount", t.getAmount());
        v.put("note", t.getNote());
        v.put("date", t.getDate());
        v.put("weekday", t.getWeekday());
        v.put("typeTransaction", t.getTypeTransaction());
        v.put("createdAt", t.getCreatedAt());

        long id = db.insert("TransactionTable", null, v);
        db.close();
        return id;
    }

    // ================= UPDATE =================
    public boolean update(long idTransaction, Transaction t) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues v = new ContentValues();
        v.put("idCategory", t.getIdCategory());
        v.put("amount", t.getAmount());
        v.put("note", t.getNote());
        v.put("date", t.getDate());
        v.put("weekday", t.getWeekday());
        v.put("typeTransaction", t.getTypeTransaction());

        int rows = db.update(
                "TransactionTable",
                v,
                "idTransaction = ?",
                new String[]{String.valueOf(idTransaction)}
        );

        db.close();
        return rows > 0;
    }

    // ================= DELETE =================
    public boolean delete(long idTransaction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                "TransactionTable",
                "idTransaction = ?",
                new String[]{String.valueOf(idTransaction)}
        );
        db.close();
        return rows > 0;
    }

    // ================= GET ALL (JOIN CATEGORY) =================
    public Cursor getAllCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery(
                "SELECT t.*, c.nameCategory, c.iconCategory " +
                        "FROM TransactionTable t " +
                        "LEFT JOIN Category c ON t.idCategory = c.idCategory " +
                        "ORDER BY t.createdAt DESC",
                null
        );
    }

    // ================= GET BY ID (EDIT) =================
    public Cursor getById(long idTransaction) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery(
                "SELECT t.*, c.nameCategory, c.iconCategory " +
                        "FROM TransactionTable t " +
                        "LEFT JOIN Category c ON t.idCategory = c.idCategory " +
                        "WHERE t.idTransaction = ?",
                new String[]{String.valueOf(idTransaction)}
        );
    }
    public Cursor getByMonth(int month, int year) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        long start = getMonthStartMillis(month, year);
        long end   = getMonthEndMillis(month, year);

        return db.rawQuery(
                "SELECT t.*, c.nameCategory, c.iconCategory " +
                        "FROM TransactionTable t " +
                        "LEFT JOIN Category c ON t.idCategory = c.idCategory " +
                        "WHERE t.createdAt >= ? AND t.createdAt < ? " +
                        "ORDER BY t.createdAt DESC",
                new String[]{
                        String.valueOf(start),
                        String.valueOf(end)
                }
        );
    }
    public double getTotalByMonth(int month, int year, String type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        long start = getMonthStartMillis(month, year);
        long end   = getMonthEndMillis(month, year);

        Cursor cursor = db.rawQuery(
                "SELECT SUM(amount) FROM TransactionTable " +
                        "WHERE typeTransaction = ? " +
                        "AND createdAt >= ? AND createdAt < ?",
                new String[]{
                        type,
                        String.valueOf(start),
                        String.valueOf(end)
                }
        );

        double total = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        return total;
    }
    public double getStartBalance(int month, int year) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        long start = getMonthStartMillis(month, year);

        Cursor cursor = db.rawQuery(
                "SELECT " +
                        "SUM(CASE WHEN typeTransaction = 'INCOME' THEN amount ELSE 0 END) - " +
                        "SUM(CASE WHEN typeTransaction = 'EXPENSE' THEN amount ELSE 0 END) " +
                        "FROM TransactionTable " +
                        "WHERE createdAt < ?",
                new String[]{String.valueOf(start)}
        );

        double balance = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            balance = cursor.getDouble(0);
        }

        cursor.close();
        return balance;
    }
    private long getMonthStartMillis(int month, int year) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month - 1, 1, 0, 0, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private long getMonthEndMillis(int month, int year) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month - 1, 1, 0, 0, 0);
        cal.add(java.util.Calendar.MONTH, 1);
        return cal.getTimeInMillis();
    }

    public Cursor getIncomeGroupByCategory(int month, int year) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        long start = getMonthStartMillis(month, year);
        long end   = getMonthEndMillis(month, year);

        return db.rawQuery(
                "SELECT c.nameCategory, SUM(t.amount) AS total " +
                        "FROM TransactionTable t " +
                        "JOIN Category c ON t.idCategory = c.idCategory " +
                        "WHERE t.typeTransaction = 'INCOME' " +
                        "AND t.createdAt >= ? AND t.createdAt < ? " +
                        "GROUP BY t.idCategory",
                new String[]{ String.valueOf(start), String.valueOf(end) }
        );
    }

    public Cursor getExpenseGroupByCategory(int month, int year) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        long start = getMonthStartMillis(month, year);
        long end   = getMonthEndMillis(month, year);

        return db.rawQuery(
                "SELECT c.nameCategory, SUM(t.amount) AS total " +
                        "FROM TransactionTable t " +
                        "JOIN Category c ON t.idCategory = c.idCategory " +
                        "WHERE t.typeTransaction = 'EXPENSE' " +
                        "AND t.createdAt >= ? AND t.createdAt < ? " +
                        "GROUP BY t.idCategory",
                new String[]{ String.valueOf(start), String.valueOf(end) }
        );
    }
    public Cursor getAllCursorByType(String type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery(
                "SELECT t.*, c.nameCategory, c.iconCategory " +
                        "FROM TransactionTable t " +
                        "LEFT JOIN Category c ON t.idCategory = c.idCategory " +
                        "WHERE t.typeTransaction = ? " +
                        "ORDER BY t.createdAt DESC",
                new String[]{ type }
        );
    }


}
