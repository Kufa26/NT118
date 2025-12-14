package com.example.login_sigup.database.User;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "login_signup.db";
    private static final int DB_VERSION = 1;

    public UserHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS USER");
        onCreate(db);
    }
}

