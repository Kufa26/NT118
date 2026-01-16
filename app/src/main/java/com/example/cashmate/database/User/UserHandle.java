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

    // DELETE USER BY EMAIL
    public boolean deleteUserByEmail(String email) {
        int rows = db.delete("USER", "email = ?", new String[]{email});
        return rows > 0;
    }

    // SIGN UP
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

        long result = db.insert("USER", null, values);
        return result != -1;
    }


    // UPDATE PASSWORD
    public void updatePassword(String uid, String newPass) {
        ContentValues values = new ContentValues();
        values.put("password", newPass);

        db.update("USER", values, "id = ?", new String[]{uid});
    }

    // CHECK EMAIL
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

    // INSERT/SYNC USER FROM FIREBASE
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
        values.put("password", password); // google hoặc rỗng
        values.put("dob", "");
        values.put("gender", "Unknown");
        values.put("country", "Việt Nam");
        values.put("phoneNumber", "");
        values.put("avatarUrl", avatarUrl);

        int updated = db.update("USER", values, "id = ?", new String[]{uid});
        if (updated == 0) {
            // insert mới, mặc định chưa login
            values.put("id", uid);
            db.insert("USER", null, values);
        }
    }

    // GET USER BY ID
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



    // CURSOR USER
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

    public User getUserByEmail(String email) {
        User user = null;
        Cursor c = db.rawQuery(
                "SELECT * FROM USER WHERE email = ?",
                new String[]{email}
        );

        if (c.moveToFirst()) {
            user = new User(
                    c.getString(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("fullName")),
                    c.getString(c.getColumnIndexOrThrow("email")),
                    c.getString(c.getColumnIndexOrThrow("password")),
                    c.getString(c.getColumnIndexOrThrow("dob")),
                    c.getString(c.getColumnIndexOrThrow("gender")),
                    c.getString(c.getColumnIndexOrThrow("country")),
                    c.getString(c.getColumnIndexOrThrow("phoneNumber")),
                    c.getString(c.getColumnIndexOrThrow("avatarUrl"))
            );
        }
        c.close();
        return user;
    }

    public boolean isUserExist(String email) {
        Cursor c = db.rawQuery(
                "SELECT 1 FROM USER WHERE email = ? LIMIT 1",
                new String[]{email}
        );
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public void updateUserInfo(
            String email,
            String fullName,
            String dob,
            String gender,
            String country,
            String phone
    ) {
        ContentValues values = new ContentValues();
        values.put("fullName", fullName);
        values.put("dob", dob);
        values.put("gender", gender);
        values.put("country", country);
        values.put("phoneNumber", phone);

        db.update(
                "USER",
                values,
                "email = ?",
                new String[]{email}
        );
    }

    public User getCurrentUser() {
        User user = null;

        Cursor c = db.query(
                "USER",
                null,
                "isLoggedIn = ?",
                new String[]{"1"},
                null,
                null,
                null,
                "1"
        );

        if (c != null && c.moveToFirst()) {
            user = new User(
                    c.getString(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("fullName")),
                    c.getString(c.getColumnIndexOrThrow("email")),
                    c.getString(c.getColumnIndexOrThrow("password")),
                    c.getString(c.getColumnIndexOrThrow("dob")),
                    c.getString(c.getColumnIndexOrThrow("gender")),
                    c.getString(c.getColumnIndexOrThrow("country")),
                    c.getString(c.getColumnIndexOrThrow("phoneNumber")),
                    c.getString(c.getColumnIndexOrThrow("avatarUrl"))
            );

            // 👇 set sau
            user.setIsLoggedIn(
                    c.getInt(c.getColumnIndexOrThrow("isLoggedIn"))
            );
        }

        if (c != null) c.close();
        return user;
    }


}
