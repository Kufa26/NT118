package com.example.cashmate.budget;

import android.content.Context;

import com.example.cashmate.R;
import com.example.cashmate.database.User.User;
import com.example.cashmate.database.User.UserHandle;
import com.example.cashmate.database.budget.Budget;
import com.example.cashmate.database.budget.BudgetHandle;

import java.util.ArrayList;
import java.util.List;

public class BudgetStorage {
    private static BudgetStorage instance;
    private BudgetHandle budgetHandle;
    private UserHandle userHandle;
    private Context context;

    private BudgetStorage() { }

    public static BudgetStorage getInstance() {
        if (instance == null) {
            instance = new BudgetStorage();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        this.budgetHandle = new BudgetHandle(context);
        this.userHandle = new UserHandle(context);
    }

    public List<BudgetItem> getList() {
        if (budgetHandle == null || userHandle == null) return new ArrayList<>();

        String userId = "unknown";
        User currentUser = userHandle.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getIdUser();
        }

        List<Budget> dbList = budgetHandle.getBudgetsByUser(userId);
        List<BudgetItem> uiList = new ArrayList<>();

        for (Budget b : dbList) {
            String categoryType = budgetHandle.getTypeForCategory(b.getIdCategory());
            if ("INCOME".equalsIgnoreCase(categoryType)) continue; // ngân sách: bỏ khoản thu

            String iconName = budgetHandle.getIconForCategory(b.getIdCategory());
            int iconRes = getDrawableId(iconName);

            uiList.add(new BudgetItem(
                    b.getIdBudget(),
                    b.getName(),
                    (long) b.getTotalAmount(),
                    (long) b.getSpentAmount(),
                    iconRes,
                    b.getTimeType()
            ));
        }
        return uiList;
    }

    private int getDrawableId(String iconName) {
        if (context == null || iconName == null) return R.drawable.ic_food;
        int resId = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
        if (resId == 0) {
            return R.drawable.ic_food;
        }
        return resId;
    }

    public void addItem(BudgetItem item) { }

    public void deleteItem(BudgetItem item) {
        if (budgetHandle != null) {
            budgetHandle.deleteBudget(item.getId());
        }
    }
}
