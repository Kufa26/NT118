package com.example.login_sigup.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "login_signup.db";
    private static final int DB_VERSION = 2; // 🔴 TĂNG VERSION

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // ================= USER =================
        String CREATE_USER_TABLE =
                "CREATE TABLE USER (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "fullName TEXT, " +
                        "email TEXT UNIQUE, " +
                        "password TEXT, " +
                        "dob TEXT, " +
                        "gender TEXT, " +
                        "country TEXT, " +
                        "phoneNumber TEXT, " +
                        "avatarUrl TEXT)";
        db.execSQL(CREATE_USER_TABLE);

        // ================= CATEGORY =================
        String CREATE_CATEGORY_TABLE =
                "CREATE TABLE Category (" +
                        "idCategory INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nameCategory TEXT, " +
                        "typeCategory TEXT, " +        // INCOME / EXPENSE
                        "iconCategory TEXT, " +
                        "idParent TEXT)";
        db.execSQL(CREATE_CATEGORY_TABLE);

        // ================= TRANSACTION =================
        String CREATE_TRANSACTION_TABLE =
                "CREATE TABLE TransactionTable (" +
                        "idTransaction INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "idUser INTEGER, " +
                        "idCategory INTEGER, " +
                        "amount REAL, " +
                        "note TEXT, " +
                        "date TEXT, " +
                        "typeTransaction TEXT, " +     // INCOME / EXPENSE
                        "FOREIGN KEY(idUser) REFERENCES USER(id), " +
                        "FOREIGN KEY(idCategory) REFERENCES Category(idCategory))";
        db.execSQL(CREATE_TRANSACTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // ⚠️ Khi nâng DB_VERSION → xóa bảng cũ để tạo lại
        db.execSQL("DROP TABLE IF EXISTS TransactionTable");
        db.execSQL("DROP TABLE IF EXISTS Category");
        db.execSQL("DROP TABLE IF EXISTS USER");

        onCreate(db);
    }
}
