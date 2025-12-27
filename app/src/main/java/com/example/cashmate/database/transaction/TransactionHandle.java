package com.example.cashmate.database.transaction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cashmate.database.DatabaseHelper;

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

    // ================= RECENT (LIMIT) =================
    public Cursor getRecentCursor(String userId, int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String where = "";
        String[] args = null;

        if (userId != null) {
            where = " WHERE t.idUser = ?";
            args = new String[]{userId};
        }

        return db.rawQuery(
                "SELECT t.*, c.nameCategory, c.iconCategory " +
                        "FROM TransactionTable t " +
                        "LEFT JOIN Category c ON t.idCategory = c.idCategory " +
                        where +
                        " ORDER BY t.createdAt DESC " +
                        "LIMIT " + limit,
                args
        );
    }

    public static class Totals {
        public double income;
        public double expense;

        public double getBalance() {
            return income - expense;
        }
    }

    public Totals getTotalsForUser(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String where = "";
        String[] args = null;
        if (userId != null) {
            where = " WHERE idUser = ?";
            args = new String[]{userId};
        }

        Cursor cursor = db.rawQuery(
                "SELECT typeTransaction, SUM(amount) AS total " +
                        "FROM TransactionTable" +
                        where +
                        " GROUP BY typeTransaction",
                args
        );

        Totals totals = new Totals();
        if (cursor.moveToFirst()) {
            do {
                String type = cursor.getString(cursor.getColumnIndexOrThrow("typeTransaction"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
                if ("INCOME".equalsIgnoreCase(type)) {
                    totals.income = total;
                } else {
                    totals.expense += total;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return totals;
    }

    // ================= MONTHLY TOTALS (group by month/year) =================
    public Cursor getMonthlyTotals(String userId, String type, int limitMonths) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder sql = new StringBuilder(
                "SELECT substr(t.date,4,2) AS month, substr(t.date,7,4) AS year, " +
                        "SUM(t.amount) AS total " +
                        "FROM TransactionTable t " +
                        "WHERE t.typeTransaction = ? "
        );

        if (userId != null) {
            sql.append("AND t.idUser = ? ");
        }

        sql.append("GROUP BY year, month ");
        sql.append("ORDER BY CAST(year AS INTEGER) DESC, CAST(month AS INTEGER) DESC ");
        sql.append("LIMIT ").append(limitMonths);

        if (userId != null) {
            return db.rawQuery(sql.toString(), new String[]{type, userId});
        } else {
            return db.rawQuery(sql.toString(), new String[]{type});
        }
    }
        // ================= FILTER BY TYPE =================
    public Cursor getAllCursorByType(String typeTransaction) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery(
                "SELECT t.*, c.nameCategory, c.iconCategory " +
                        "FROM TransactionTable t " +
                        "LEFT JOIN Category c ON t.idCategory = c.idCategory " +
                        "WHERE t.typeTransaction = ? " +
                        "ORDER BY t.createdAt DESC",
                new String[]{typeTransaction}
        );
    }

    // ================= BY MONTH (LIST) =================
    public Cursor getByMonth(int month, int year) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery(
                "SELECT t.*, c.nameCategory, c.iconCategory " +
                        "FROM TransactionTable t " +
                        "LEFT JOIN Category c ON t.idCategory = c.idCategory " +
                        "WHERE substr(t.date,4,2) = ? AND substr(t.date,7,4) = ? " +
                        "ORDER BY t.createdAt DESC",
                new String[]{
                        String.format("%02d", month),
                        String.valueOf(year)
                }
        );
    }

    // ================= TOTAL BY MONTH =================
    public double getTotalByMonth(int month, int year, String type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT SUM(amount) AS total " +
                        "FROM TransactionTable " +
                        "WHERE typeTransaction = ? " +
                        "AND substr(date,4,2) = ? " +
                        "AND substr(date,7,4) = ?",
                new String[]{
                        type,
                        String.format("%02d", month),
                        String.valueOf(year)
                }
        );
        try {
            if (c.moveToFirst() && !c.isNull(c.getColumnIndexOrThrow("total"))) {
                return c.getDouble(c.getColumnIndexOrThrow("total"));
            }
            return 0;
        } finally {
            c.close();
        }
    }

    // ================= START BALANCE BEFORE MONTH =================
    public double getStartBalance(int month, int year) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT SUM(CASE WHEN typeTransaction = 'INCOME' THEN amount ELSE -amount END) AS balance " +
                        "FROM TransactionTable " +
                        "WHERE CAST(substr(date,7,4) AS INTEGER) < ? " +
                        "OR (CAST(substr(date,7,4) AS INTEGER) = ? AND CAST(substr(date,4,2) AS INTEGER) < ?)",
                new String[]{
                        String.valueOf(year),
                        String.valueOf(year),
                        String.valueOf(month)
                }
        );
        try {
            if (c.moveToFirst() && !c.isNull(c.getColumnIndexOrThrow("balance"))) {
                return c.getDouble(c.getColumnIndexOrThrow("balance"));
            }
            return 0;
        } finally {
            c.close();
        }
    }

}
