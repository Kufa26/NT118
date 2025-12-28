package com.example.cashmate.database.Category;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cashmate.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CategoryHandle {

    private final DatabaseHelper dbHelper;

    public CategoryHandle(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // ================= INSERT =================
    public long insertCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nameCategory", category.getNameCategory());
        values.put("typeCategory", category.getTypeCategory());
        values.put("iconCategory", category.getIconCategory());

        long id = db.insert("Category", null, values);
        db.close();
        return id;
    }

    // ================= GET BY TYPE =================
    public List<Category> getCategoriesByType(String type) {
        List<Category> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM Category WHERE typeCategory = ?",
                new String[]{type}
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(new Category(
                        cursor.getLong(cursor.getColumnIndexOrThrow("idCategory")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nameCategory")),
                        cursor.getString(cursor.getColumnIndexOrThrow("typeCategory")),
                        cursor.getString(cursor.getColumnIndexOrThrow("iconCategory"))
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    // ================= DELETE (CASCADE TRANSACTION) =================
    public boolean deleteCategory(long idCategory) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // Xóa giao dịch thuộc nhóm
            db.delete(
                    "TransactionTable",
                    "idCategory = ?",
                    new String[]{String.valueOf(idCategory)}
            );

            // Xóa nhóm
            int rows = db.delete(
                    "Category",
                    "idCategory = ?",
                    new String[]{String.valueOf(idCategory)}
            );

            db.setTransactionSuccessful();
            return rows > 0;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // ================= INSERT DEFAULT CATEGORY (FIXED) =================
    public void insertDefaultCategoriesIfEmpty() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 🔥 KHÔNG DÙNG COUNT(*)
        // 🔥 KIỂM TRA THEO NHÓM MẶC ĐỊNH CỤ THỂ
        Cursor c = db.rawQuery(
                "SELECT 1 FROM Category WHERE nameCategory = ? LIMIT 1",
                new String[]{"Ăn uống"}
        );

        if (c.moveToFirst()) {
            c.close();
            db.close();
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

        db.close();
    }

    // ================= HELPER =================
    private void insert(SQLiteDatabase db, String name, String type, String icon) {
        ContentValues v = new ContentValues();
        v.put("nameCategory", name);
        v.put("typeCategory", type);
        v.put("iconCategory", icon);
        db.insert("Category", null, v);
    }
}
