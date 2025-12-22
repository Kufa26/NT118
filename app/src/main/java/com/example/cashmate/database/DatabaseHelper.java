package com.example.cashmate.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "login_signup.db";
    private static final int DB_VERSION = 6;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USER_TABLE =
                "CREATE TABLE IF NOT EXISTS USER (" +
                        "id TEXT PRIMARY KEY, " +
                        "fullName TEXT, " +
                        "email TEXT UNIQUE, " +
                        "password TEXT, " +
                        "dob TEXT, " +
                        "gender TEXT, " +
                        "country TEXT, " +
                        "phoneNumber TEXT, " +
                        "avatarUrl TEXT, " +
                        "isLoggedIn INTEGER DEFAULT 0)";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_CATEGORY_TABLE =
                "CREATE TABLE IF NOT EXISTS Category (" +
                        "idCategory INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nameCategory TEXT, " +
                        "typeCategory TEXT, " +
                        "iconCategory TEXT)";
        db.execSQL(CREATE_CATEGORY_TABLE);

        String CREATE_TRANSACTION_TABLE =
                "CREATE TABLE IF NOT EXISTS TransactionTable (" +
                        "idTransaction INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "idUser TEXT, " +
                        "idCategory INTEGER, " +
                        "amount REAL, " +
                        "note TEXT, " +
                        "date TEXT, " +
                        "weekday TEXT, " +
                        "typeTransaction TEXT, " +
                        "createdAt INTEGER, " +
                        "FOREIGN KEY(idUser) REFERENCES USER(id), " +
                        "FOREIGN KEY(idCategory) REFERENCES Category(idCategory))";
        db.execSQL(CREATE_TRANSACTION_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        ensureIsLoggedInColumn(db);
    }

    private void ensureIsLoggedInColumn(SQLiteDatabase db) {
        Cursor c = null;
        try {
            c = db.rawQuery("PRAGMA table_info(USER)", null);
            boolean hasColumn = false;
            while (c.moveToNext()) {
                String colName = c.getString(c.getColumnIndexOrThrow("name"));
                if ("isLoggedIn".equalsIgnoreCase(colName)) {
                    hasColumn = true;
                    break;
                }
            }
            if (!hasColumn) {
                db.execSQL("ALTER TABLE USER ADD COLUMN isLoggedIn INTEGER DEFAULT 0");
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nếu m muốn giữ data, đừng DROP. Nhưng để đơn giản và tránh lỗi schema:
        db.execSQL("DROP TABLE IF EXISTS TransactionTable");
        db.execSQL("DROP TABLE IF EXISTS Category");
        db.execSQL("DROP TABLE IF EXISTS USER");
        onCreate(db);
    }
}
