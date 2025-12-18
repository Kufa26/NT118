package com.example.login_sigup.database.transaction;

public class Transaction {
    private Long idTransaction;
    private Long idUser;
    private Long idCategory;
    private Double amount;
    private String note;
    private String date;
    private String typeTransaction;

    public Transaction(Long idTransaction, Long idUser, Long idCategory,
                       double amount, String note, String date, String typeTransaction) {
        super();
        this.idTransaction = idTransaction;
        this.idUser = idUser;
        this.idCategory = idCategory;
        this.amount = amount;
        this.note = note;
        this.date = date;
        this.typeTransaction = typeTransaction;
    }

    public Transaction(Long idUser, Long idCategory, double amount, String note, String date, String typeTransaction) {
        super();
        this.idUser = idUser;
        this.idCategory = idCategory;
        this.amount = amount;
        this.note = note;
        this.date = date;
        this.typeTransaction = typeTransaction;
    }

    public Long getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(Long idTransaction) {
        this.idTransaction = idTransaction;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public Long getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Long idCategory) {
        this.idCategory = idCategory;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(String typeTransaction) {
        this.typeTransaction = typeTransaction;
    }
}


