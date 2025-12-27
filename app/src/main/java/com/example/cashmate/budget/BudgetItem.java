package com.example.cashmate.budget;

public class BudgetItem {
    private int id; // Thêm ID để phục vụ việc xóa
    private String name;
    private long totalAmount;
    private long spentAmount;
    private int iconRes;
    private String timeType;

    // --- CONSTRUCTOR 1: ĐẦY ĐỦ (6 tham số) - Dùng khi lấy từ Database ---
    public BudgetItem(int id, String name, long totalAmount, long spentAmount, int iconRes, String timeType) {
        this.id = id;
        this.name = name;
        this.totalAmount = totalAmount;
        this.spentAmount = spentAmount;
        this.iconRes = iconRes;
        this.timeType = timeType;
    }

    // --- CONSTRUCTOR 2: RÚT GỌN (5 tham số) - Dùng khi tạo mới tạm thời ---
    public BudgetItem(String name, long totalAmount, long spentAmount, int iconRes, String timeType) {
        this.name = name;
        this.totalAmount = totalAmount;
        this.spentAmount = spentAmount;
        this.iconRes = iconRes;
        this.timeType = timeType;
    }

    // --- GETTERS ---
    public int getId() { return id; }
    public String getName() { return name; }
    public long getTotalAmount() { return totalAmount; }
    public long getSpentAmount() { return spentAmount; }
    public int getIconRes() { return iconRes; }
    public String getTimeType() { return timeType; }
}