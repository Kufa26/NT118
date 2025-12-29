package com.example.cashmate.database.transaction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cashmate.database.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;
import java.util.TimeZone;

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

    public static class TopExpense {
        public final String nameCategory;
        public final String iconCategory;
        public final double total;

        public TopExpense(String nameCategory, String iconCategory, double total) {
            this.nameCategory = nameCategory;
            this.iconCategory = iconCategory;
            this.total = total;
        }
    }

    public Totals getTotalsForUser(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String where = "";
        String[] args = null;
        if (userId != null) {
            where = " WHERE (idUser = ? OR idUser IS NULL OR idUser = '')";
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

    // ================= DATA FIX: SYNC createdAt WITH date FIELD =================
    public void normalizeCreatedAtFromDate() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery(
                "SELECT idTransaction, date, createdAt FROM TransactionTable WHERE date IS NOT NULL",
                null
        );
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false);sdf.setTimeZone(TimeZone.getDefault());

        try {
            while (c.moveToNext()) {
                long id = c.getLong(c.getColumnIndexOrThrow("idTransaction"));
                String dateStr = c.getString(c.getColumnIndexOrThrow("date"));
                Long currentCreated = c.isNull(c.getColumnIndexOrThrow("createdAt"))
                        ? null
                        : c.getLong(c.getColumnIndexOrThrow("createdAt"));

                try {
                    Date parsed = sdf.parse(dateStr);
                    long targetCreated = parsed.getTime();
                    if (currentCreated == null || currentCreated != targetCreated) {
                        ContentValues v = new ContentValues();
                        v.put("createdAt", targetCreated);
                        db.update(
                                "TransactionTable",
                                v,
                                "idTransaction = ?",
                                new String[]{String.valueOf(id)}
                        );
                    }
                } catch (ParseException ignored) {
                    // skip rows with invalid date format
                }
            }
        } finally {
            c.close();
        }
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

    // ================= TOP EXPENSES BY CATEGORY =================
    public List<TopExpense> getTopExpenses(String userId, long startMillis, long endMillis, int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Clamp limit to avoid invalid or empty LIMIT clause
        int limitSafe = Math.max(1, limit);

        StringBuilder sql = new StringBuilder(
                "SELECT c.nameCategory, c.iconCategory, SUM(t.amount) AS total " +
                        "FROM TransactionTable t " +
                        "LEFT JOIN Category c ON t.idCategory = c.idCategory " +
                        "WHERE UPPER(TRIM(IFNULL(t.typeTransaction,''))) = 'EXPENSE' " +
                        "AND t.createdAt BETWEEN ? AND ? "
        );

        List<String> args = new ArrayList<>();
        args.add(String.valueOf(startMillis));
        args.add(String.valueOf(endMillis));

        if (userId != null) {
            sql.append("AND (t.idUser = ? OR t.idUser IS NULL OR t.idUser = '') ");
            args.add(userId);
        }

        sql.append("GROUP BY c.nameCategory, c.iconCategory ");
        sql.append("ORDER BY total DESC ");
        sql.append("LIMIT ").append(limitSafe);

        Cursor c = db.rawQuery(sql.toString(), args.toArray(new String[0]));

        List<TopExpense> result = new ArrayList<>();
        try {
            if (c.moveToFirst()) {
                do {
                    String name = c.isNull(c.getColumnIndexOrThrow("nameCategory"))
                            ? null
                            : c.getString(c.getColumnIndexOrThrow("nameCategory"));
                    String icon = c.isNull(c.getColumnIndexOrThrow("iconCategory"))
                            ? null
                            : c.getString(c.getColumnIndexOrThrow("iconCategory"));
                    double total = c.getDouble(c.getColumnIndexOrThrow("total"));
                    result.add(new TopExpense(name, icon, total));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }

        return result;
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
        return getByMonth(month, year, null);
    }

    public Cursor getByMonth(int month, int year, String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long start = monthStartMillis(month, year);
        long end = monthEndMillis(month, year);

        StringBuilder sql = new StringBuilder(
                "SELECT t.*, c.nameCategory, c.iconCategory " +
                        "FROM TransactionTable t " +
                        "LEFT JOIN Category c ON t.idCategory = c.idCategory " +
                        "WHERE t.createdAt BETWEEN ? AND ? "
        );
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(start));
        args.add(String.valueOf(end));

        if (userId != null) {
            sql.append("AND (t.idUser = ? OR t.idUser IS NULL OR t.idUser = '') ");
            args.add(userId);
        }

        sql.append("ORDER BY t.createdAt DESC, t.idTransaction DESC");

        return db.rawQuery(sql.toString(), args.toArray(new String[0]));
    }

    // ================= COUNT BY CATEGORY =================
    public int countByCategory(Long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) AS cnt FROM TransactionTable WHERE idCategory = ?",
                new String[]{String.valueOf(categoryId)}
        );
        try {
            if (c.moveToFirst()) {
                return c.getInt(c.getColumnIndexOrThrow("cnt"));
            }
            return 0;
        } finally {
            c.close();
        }
    }

    // ================= LIST BY CATEGORY =================
    public Cursor getByCategory(Long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery(
                "SELECT t.*, c.nameCategory, c.iconCategory " +
                        "FROM TransactionTable t " +
                        "LEFT JOIN Category c ON t.idCategory = c.idCategory " +
                        "WHERE t.idCategory = ? " +
                        "ORDER BY t.createdAt DESC",
                new String[]{String.valueOf(categoryId)}
        );
    }

    // ================= TOTAL BY MONTH =================
    public double getTotalByMonth(int month, int year, String type) {
        return getTotalByMonth(month, year, type, null);
    }

    public double getTotalByMonth(int month, int year, String type, String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long start = monthStartMillis(month, year);
        long end = monthEndMillis(month, year);

        StringBuilder sql = new StringBuilder(
                "SELECT SUM(amount) AS total " +
                        "FROM TransactionTable " +
                        "WHERE typeTransaction = ? " +
                        "AND createdAt BETWEEN ? AND ? "
        );

        List<String> args = new ArrayList<>();
        args.add(type);
        args.add(String.valueOf(start));
        args.add(String.valueOf(end));

        if (userId != null) {
            sql.append("AND (idUser = ? OR idUser IS NULL OR idUser = '') ");
            args.add(userId);
        }

        Cursor c = db.rawQuery(sql.toString(), args.toArray(new String[0]));
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
        return getStartBalance(month, year, null);
    }

    public double getStartBalance(int month, int year, String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long monthStart = monthStartMillis(month, year);

        StringBuilder sql = new StringBuilder(
                "SELECT SUM(CASE WHEN typeTransaction = 'INCOME' THEN amount ELSE -amount END) AS balance " +
                        "FROM TransactionTable WHERE createdAt < ? "
        );
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(monthStart));

        if (userId != null) {
            sql.append("AND (idUser = ? OR idUser IS NULL OR idUser = '') ");
            args.add(userId);
        }

        Cursor c = db.rawQuery(sql.toString(), args.toArray(new String[0]));
        try {
            if (c.moveToFirst() && !c.isNull(c.getColumnIndexOrThrow("balance"))) {
                return c.getDouble(c.getColumnIndexOrThrow("balance"));
            }
            return 0;
        } finally {
            c.close();
        }
    }

    private long monthStartMillis(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private long monthEndMillis(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }

}
