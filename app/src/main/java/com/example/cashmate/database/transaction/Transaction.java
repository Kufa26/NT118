package com.example.cashmate.database.transaction;

public class Transaction {

    private Long idTransaction;
    private Long idUser;
    private Long idCategory;
    private Double amount;
    private String note;

    // date hiển thị (vd: dd-MM-yyyy hoặc dd/MM/yyyy)
    private String date;

    private String weekday;          // Thứ Hai, Thứ Ba, ...
    private String typeTransaction;  // INCOME / EXPENSE
    private Long createdAt;           // dùng sort, thống kê

    // ================= CONSTRUCTOR (QUERY) =================
    public Transaction(Long idTransaction,
                       Long idUser,
                       Long idCategory,
                       Double amount,
                       String note,
                       String date,
                       String weekday,
                       String typeTransaction,
                       Long createdAt) {

        this.idTransaction = idTransaction;
        this.idUser = idUser;
        this.idCategory = idCategory;
        this.amount = amount;
        this.note = note;
        this.date = date;
        this.weekday = weekday;
        this.typeTransaction = typeTransaction;
        this.createdAt = createdAt;
    }

    // ================= CONSTRUCTOR (INSERT) =================
    public Transaction(Long idUser,
                       Long idCategory,
                       Double amount,
                       String note,
                       String date,
                       String weekday,
                       String typeTransaction,
                       Long createdAt) {

        this.idUser = idUser;
        this.idCategory = idCategory;
        this.amount = amount;
        this.note = note;
        this.date = date;
        this.weekday = weekday;
        this.typeTransaction = typeTransaction;
        this.createdAt = createdAt;
    }

    // ================= GETTERS =================

    public Long getIdTransaction() {
        return idTransaction;
    }

    public Long getIdUser() {
        return idUser;
    }

    public Long getIdCategory() {
        return idCategory;
    }

    public Double getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    public String getWeekday() {
        return weekday;
    }

    public String getTypeTransaction() {
        return typeTransaction;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    // ================= SETTERS (OPTIONAL) =================

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public void setTypeTransaction(String typeTransaction) {
        this.typeTransaction = typeTransaction;
    }
}
