package com.example.login_sigup.database.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.login_sigup.database.User.UserHelper;

public class UserHandle {

    private SQLiteDatabase db;

    public UserHandle(Context context) {
        UserHelper helper = new UserHelper(context);
        db = helper.getWritableDatabase();
    }

    // ===== SIGN UP =====
    public boolean handleSignUp(User user) {
        ContentValues values = new ContentValues();
        values.put("fullName", user.getFullName());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("dob", user.getDob());
        values.put("gender", user.getGender());
        values.put("country", user.getCountry());
        values.put("phoneNumber", user.getPhoneNumber());
        values.put("avatarUrl", user.getAvatarUrl());

        long result = db.insert("USER", null, values);
        return result != -1;
    }

    // ===== LOGIN =====
    public User handleLogin(String email, String password) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM USER WHERE email = ? AND password = ?",
                new String[]{email, password}
        );

        if (cursor.moveToFirst()) {
            User user = new User(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8)
            );
            cursor.close();
            return user;
        }

        cursor.close();
        return null;
    }

    // ===== CHECK EMAIL =====
    public boolean handleCheckEmailExists(String email) {
        Cursor cursor = db.rawQuery(
                "SELECT id FROM USER WHERE email = ?",
                new String[]{email}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
}
