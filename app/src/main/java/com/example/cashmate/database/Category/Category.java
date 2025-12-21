package com.example.cashmate.database.Category;

public class Category {
    private Long idCategory;
    private String nameCategory;
    private String typeCategory;
    private String iconCategory;


    public Category(Long idCategory, String nameCategory, String typeCategory, String iconCategory) {
        this.idCategory = idCategory;
        this.nameCategory = nameCategory;
        this.typeCategory = typeCategory;
        this.iconCategory = iconCategory;
    }

    public Category(String nameCategory, String typeCategory, String iconCategory) {
        this.nameCategory = nameCategory;
        this.typeCategory = typeCategory;
        this.iconCategory = iconCategory;
    }


    public Long getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Long idCategory) {
        this.idCategory = idCategory;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public String getTypeCategory() {
        return typeCategory;
    }

    public void setTypeCategory(String typeCategory) {
        this.typeCategory = typeCategory;
    }

    public String getIconCategory() {
        return iconCategory;
    }

    public void setIconCategory(String iconCategory) {
        this.iconCategory = iconCategory;
    }

}
