package com.example.cashmate.database.budget;

public class Budget {
    private int idBudget;
    private String idUser;
    private int idCategory;
    private String name;
    private double totalAmount;
    private double spentAmount;
    private String startDate;
    private String endDate;
    private String timeType;

    public Budget(int idBudget, String idUser, int idCategory, String name,
                  double totalAmount, double spentAmount,
                  String startDate, String endDate, String timeType) {
        this.idBudget = idBudget;
        this.idUser = idUser;
        this.idCategory = idCategory;
        this.name = name;
        this.totalAmount = totalAmount;
        this.spentAmount = spentAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeType = timeType;
    }

    public Budget(String idUser, int idCategory, String name,
                  double totalAmount, double spentAmount,
                  String startDate, String endDate, String timeType) {
        this.idUser = idUser;
        this.idCategory = idCategory;
        this.name = name;
        this.totalAmount = totalAmount;
        this.spentAmount = spentAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeType = timeType;
    }

    // Getters & Setters
    public int getIdBudget() { return idBudget; }
    public String getIdUser() { return idUser; }
    public int getIdCategory() { return idCategory; }
    public String getName() { return name; }
    public double getTotalAmount() { return totalAmount; }
    public double getSpentAmount() { return spentAmount; }
    public void setSpentAmount(double spentAmount) { this.spentAmount = spentAmount; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getTimeType() { return timeType; }
}