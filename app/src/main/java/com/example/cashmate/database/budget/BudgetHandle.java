package com.example.cashmate.database.budget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.cashmate.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BudgetHandle {

    private final DatabaseHelper dbHelper;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public BudgetHandle(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertBudget(Budget budget) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idUser", budget.getIdUser());
        values.put("idCategory", budget.getIdCategory());
        values.put("name", budget.getName());
        values.put("totalAmount", budget.getTotalAmount());
        values.put("spentAmount", 0);
        values.put("startDate", budget.getStartDate());
        values.put("endDate", budget.getEndDate());
        values.put("timeType", budget.getTimeType());

        long id = db.insert("Budget", null, values);
        db.close();
        return id;
    }

    public List<Budget> getBudgetsByUser(String idUser) {
        List<Budget> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Budget WHERE idUser = ?", new String[]{idUser});

        if (cursor.moveToFirst()) {
            do {
                list.add(new Budget(
                        cursor.getInt(0), // idBudget
                        cursor.getString(1), // idUser
                        cursor.getInt(2), // idCategory
                        cursor.getString(3), // name
                        cursor.getDouble(4), // totalAmount
                        cursor.getDouble(5), // spentAmount
                        cursor.getString(6), // startDate
                        cursor.getString(7), // endDate
                        cursor.getString(8)  // timeType
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // LẤY ICON AN TOÀN
    public String getIconForCategory(int idCategory) {
        String icon = "ic_food"; // Mặc định
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT iconCategory FROM Category WHERE idCategory = ?", new String[]{String.valueOf(idCategory)});
            if (cursor.moveToFirst()) {
                icon = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return icon;
    }

    public String getTypeForCategory(int idCategory) {
        String type = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT typeCategory FROM Category WHERE idCategory = ?",
                    new String[]{String.valueOf(idCategory)}
            );
            if (cursor.moveToFirst()) {
                type = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return type;
    }

    public void updateBudgetUsage(String idUser, int idCategory, String categoryName, double amount, String transactionDateStr) {
        List<Budget> budgets = getBudgetsByUser(idUser);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            Date transDate = sdf.parse(transactionDateStr);

            for (Budget budget : budgets) {
                Date startDate = sdf.parse(budget.getStartDate());
                Date endDate = sdf.parse(budget.getEndDate());

                if (transDate != null && !transDate.before(startDate) && !transDate.after(endDate)) {
                    boolean isIdMatch = (budget.getIdCategory() == idCategory && idCategory != 0);
                    boolean isNameMatch = (categoryName != null && budget.getName().equalsIgnoreCase(categoryName));

                    if (isIdMatch || isNameMatch) {
                        double newSpent = budget.getSpentAmount() + amount;
                        ContentValues values = new ContentValues();
                        values.put("spentAmount", newSpent);
                        db.update("Budget", values, "idBudget = ?", new String[]{String.valueOf(budget.getIdBudget())});
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public boolean deleteBudget(int idBudget) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("Budget", "idBudget = ?", new String[]{String.valueOf(idBudget)});
        db.close();
        return rows > 0;
    }
}
