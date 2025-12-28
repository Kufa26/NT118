package com.example.cashmate.budget;

import android.content.Context;
import com.example.cashmate.R;
import com.example.cashmate.database.budget.Budget;
import com.example.cashmate.database.budget.BudgetHandle;
import com.example.cashmate.database.User.User;
import com.example.cashmate.database.User.UserHandle;

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

            int iconRes = getIconFromBudgetName(b.getName());

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

    private int getIconFromBudgetName(String name) {
        if (name == null) return R.drawable.ic_food;

        String lowerName = name.toLowerCase().trim();


        if (lowerName.contains("ăn") || lowerName.contains("uống") || lowerName.contains("cafe") || lowerName.contains("food")) {
            return R.drawable.ic_food;
        }


        if (lowerName.contains("xe") || lowerName.contains("xăng") || lowerName.contains("đi lại") || lowerName.contains("di chuyển")) {
            return getDrawableId("ic_move");
        }


        if (lowerName.contains("vật nuôi") || lowerName.contains("thú cưng") || lowerName.contains("mèo") || lowerName.contains("chó")) {
            return getDrawableId("ic_pets");
        }


        if (lowerName.contains("nhà") || lowerName.contains("điện") || lowerName.contains("nước")) {
            return getDrawableId("ic_home");
        }

        if (lowerName.contains("bảo dưỡng xe") ) {
            return getDrawableId("ic_maintenance");
        }


        if (lowerName.contains("đầu tư")) {
            return getDrawableId("ic_bills");
        }

        if (lowerName.contains("bảo hiểm")) {
            return getDrawableId("ic_insurance");
        }

        if (lowerName.contains("sửa và trang trí nhà")) {
            return getDrawableId("ic_tool");
        }

        if (lowerName.contains("giải trí")) {
            return getDrawableId("ic_entertainment");
        }

        if (lowerName.contains("công việc")) {
            return getDrawableId("ic_work");
        }


        return R.drawable.ic_food;
    }

    private int getDrawableId(String iconName) {
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