package com.example.cashmate.budget;

public class BudgetItem {
    private int id;
    private String name;
    private long totalAmount;
    private long spentAmount;
    private int iconRes;
    private String timeType;

    public BudgetItem(int id, String name, long totalAmount, long spentAmount, int iconRes, String timeType) {
        this.id = id;
        this.name = name;
        this.totalAmount = totalAmount;
        this.spentAmount = spentAmount;
        this.iconRes = iconRes;
        this.timeType = timeType;
    }

    public BudgetItem(String name, long totalAmount, long spentAmount, int iconRes, String timeType) {
        this.name = name;
        this.totalAmount = totalAmount;
        this.spentAmount = spentAmount;
        this.iconRes = iconRes;
        this.timeType = timeType;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public long getTotalAmount() { return totalAmount; }
    public long getSpentAmount() { return spentAmount; }
    public int getIconRes() { return iconRes; }
    public String getTimeType() { return timeType; }
}