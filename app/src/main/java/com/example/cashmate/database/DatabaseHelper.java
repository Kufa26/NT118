package com.example.cashmate.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "login_signup.db";
    private static final int DB_VERSION = 7;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // ================= CREATE DATABASE =================
    @Override
    public void onCreate(SQLiteDatabase db) {

        // ===== USER =====
        db.execSQL(
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
                        "isLoggedIn INTEGER DEFAULT 0)"
        );

        // ===== CATEGORY (MASTER DATA) =====
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Category (" +
                        "idCategory INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nameCategory TEXT, " +
                        "typeCategory TEXT, " +
                        "iconCategory TEXT)"
        );

        // 🔥 INSERT DEFAULT CATEGORY (RUN 1 TIME PER INSTALL)
        insertDefaultCategories(db);

        // ===== TRANSACTION =====
        db.execSQL(
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
                        "FOREIGN KEY(idCategory) REFERENCES Category(idCategory))"
        );

        // ===== BUDGET =====
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Budget (" +
                        "idBudget INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "idUser TEXT, " +
                        "idCategory INTEGER, " +
                        "name TEXT, " +
                        "totalAmount REAL, " +
                        "spentAmount REAL DEFAULT 0, " +
                        "startDate TEXT, " +
                        "endDate TEXT, " +
                        "timeType TEXT, " +
                        "FOREIGN KEY(idUser) REFERENCES USER(id), " +
                        "FOREIGN KEY(idCategory) REFERENCES Category(idCategory))"
        );
    }

    // ================= OPEN DATABASE =================
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        ensureIsLoggedInColumn(db);
    }

    // ================= ENSURE COLUMN =================
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
        } finally {
            if (c != null) c.close();
        }
    }

    // ================= DATABASE UPGRADE =================
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // ❗ CHỈ DROP DỮ LIỆU ĐỘNG
        db.execSQL("DROP TABLE IF EXISTS TransactionTable");
        db.execSQL("DROP TABLE IF EXISTS Budget");

        // ❌ KHÔNG DROP Category
        // ❌ KHÔNG DROP USER

        onCreate(db);
    }

    // ================= DEFAULT CATEGORY =================
    private void insertDefaultCategories(SQLiteDatabase db) {

        // Nếu đã có "Ăn uống" → coi như đã init
        Cursor c = db.rawQuery(
                "SELECT 1 FROM Category WHERE nameCategory = ? LIMIT 1",
                new String[]{"Ăn uống"}
        );

        if (c.moveToFirst()) {
            c.close();
            return;
        }
        c.close();

        // ===== EXPENSE =====
        insert(db, "Ăn uống", "EXPENSE", "ic_food");
        insert(db, "Bảo hiểm", "EXPENSE", "ic_insurance");
        insert(db, "Đầu tư", "EXPENSE", "ic_bills");
        insert(db, "Di chuyển", "EXPENSE", "ic_move");
        insert(db, "Bảo dưỡng xe", "EXPENSE", "ic_maintenance");
        insert(db, "Vật nuôi", "EXPENSE", "ic_pets");
        insert(db, "Sửa & trang trí nhà", "EXPENSE", "ic_tool");
        insert(db, "Giải trí", "EXPENSE", "ic_entertainment");
        insert(db, "Công việc", "EXPENSE", "ic_work");
        insert(db, "Vui chơi", "EXPENSE", "ic_sports");
        insert(db, "Giáo dục", "EXPENSE", "ic_education");
        insert(db, "Hóa đơn tiện ích", "EXPENSE", "ic_bills");
        insert(db, "Hóa đơn điện", "EXPENSE", "ic_electric");
        insert(db, "Hóa đơn xăng", "EXPENSE", "ic_fuel");
        insert(db, "Hóa đơn Internet", "EXPENSE", "ic_internet");
        insert(db, "Hóa đơn nước", "EXPENSE", "ic_water");
        insert(db, "Hóa đơn điện thoại", "EXPENSE", "ic_phone");
        insert(db, "Mua sắm", "EXPENSE", "ic_shopping");
        insert(db, "Đồ dùng cá nhân", "EXPENSE", "ic_personal_items");
        insert(db, "Thuế", "EXPENSE", "ic_tax");
        insert(db, "Làm đẹp", "EXPENSE", "ic_jewelry");
        insert(db, "Vườn", "EXPENSE", "ic_garden");
        insert(db, "Sức khỏe", "EXPENSE", "ic_health");
        insert(db, "Trả nợ", "EXPENSE", "ic_debt_repayment");

        // ===== INCOME =====
        insert(db, "Lương", "INCOME", "ic_salary");
        insert(db, "Thu nợ", "INCOME", "ic_debt_collection");
    }

    // ================= HELPER =================
    private void insert(SQLiteDatabase db, String name, String type, String icon) {
        db.execSQL(
                "INSERT INTO Category(nameCategory, typeCategory, iconCategory) VALUES(?,?,?)",
                new Object[]{name, type, icon}
        );
    }
}
