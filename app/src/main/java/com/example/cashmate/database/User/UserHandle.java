package com.example.cashmate.database.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cashmate.database.DatabaseHelper;

public class UserHandle {

    private final SQLiteDatabase db;

    public UserHandle(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    // ===== DELETE USER BY EMAIL =====
    public boolean deleteUserByEmail(String email) {
        int rows = db.delete("USER", "email = ?", new String[]{email});
        return rows > 0;
    }

    // ===== SIGN UP =====
    public boolean handleSignUp(User user) {
        ContentValues values = new ContentValues();
        values.put("id", user.getIdUser());
        values.put("fullName", user.getFullName());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("dob", user.getDob());
        values.put("gender", user.getGender());
        values.put("country", user.getCountry());
        values.put("phoneNumber", user.getPhoneNumber());
        values.put("avatarUrl", user.getAvatarUrl());
        values.put("isLoggedIn", 0);

        long result = db.insert("USER", null, values);
        return result != -1;
    }

    // ===== LOGIN (EMAIL/PASS LOCAL) =====
    public User handleLogin(String email, String password) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM USER WHERE email = ? AND password = ?",
                new String[]{email, password}
        );

        try {
            if (cursor.moveToFirst()) {
                User user = mapCursorToUser(cursor);
                setCurrentUser(user.getIdUser());
                return user;
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    // ===== UPDATE PASSWORD =====
    public void updatePassword(String uid, String newPass) {
        ContentValues values = new ContentValues();
        values.put("password", newPass);

        db.update("USER", values, "id = ?", new String[]{uid});
    }

    // ===== CHECK EMAIL =====
    public boolean handleCheckEmailExists(String email) {
        Cursor cursor = db.rawQuery(
                "SELECT id FROM USER WHERE email = ?",
                new String[]{email}
        );
        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    public boolean isUserExistsById(String uid) {
        Cursor cursor = db.rawQuery(
                "SELECT id FROM USER WHERE id = ?",
                new String[]{uid}
        );
        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    // ===== INSERT/SYNC USER FROM FIREBASE=====
    public void insertUserFromFirebase(
            String uid,
            String fullName,
            String email,
            String password,
            String avatarUrl
    ) {
        // update nếu có
        ContentValues values = new ContentValues();
        values.put("fullName", fullName);
        values.put("email", email);
        values.put("password", password); // "google" hoặc rỗng
        values.put("dob", "");
        values.put("gender", "Unknown");
        values.put("country", "Vietnam");
        values.put("phoneNumber", "");
        values.put("avatarUrl", avatarUrl);

        int updated = db.update("USER", values, "id = ?", new String[]{uid});
        if (updated == 0) {
            // insert mới, mặc định chưa login
            values.put("id", uid);
            values.put("isLoggedIn", 0);
            db.insert("USER", null, values);
        }
    }

    // ===== GET USER BY ID =====
    public User getUserById(String uid) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM USER WHERE id = ?",
                new String[]{uid}
        );
        try {
            if (cursor.moveToFirst()) return mapCursorToUser(cursor);
            return null;
        } finally {
            cursor.close();
        }
    }

    // ===== GET CURRENT USER =====
    public User getCurrentUser() {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM USER WHERE isLoggedIn = 1 LIMIT 1",
                null
        );
        try {
            if (cursor.moveToFirst()) return mapCursorToUser(cursor);
            return null;
        } finally {
            cursor.close();
        }
    }

    // ===== SET CURRENT USER (CHỈ GỌI SAU KHI LOGIN SUCCESS) =====
    public boolean setCurrentUser(String uid) {
        resetLoginState();
        ContentValues values = new ContentValues();
        values.put("isLoggedIn", 1);
        int rows = db.update("USER", values, "id = ?", new String[]{uid});
        return rows > 0;
    }

    // ===== LOGOUT LOCAL =====
    public void logout() {
        resetLoginState();
    }

    // ===== RESET LOGIN STATE =====
    private void resetLoginState() {
        ContentValues reset = new ContentValues();
        reset.put("isLoggedIn", 0);
        db.update("USER", reset, null, null);
    }

    // ===== CURSOR → USER (an toàn: chỉ đọc 9 cột đầu, bỏ qua isLoggedIn) =====
    private User mapCursorToUser(Cursor cursor) {
        return new User(
                cursor.getString(0), // id
                cursor.getString(1), // fullName
                cursor.getString(2), // email
                cursor.getString(3), // password
                cursor.getString(4), // dob
                cursor.getString(5), // gender
                cursor.getString(6), // country
                cursor.getString(7), // phoneNumber
                cursor.getString(8)  // avatarUrl
        );
    }
}
