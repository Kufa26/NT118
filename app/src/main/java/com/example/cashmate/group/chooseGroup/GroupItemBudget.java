package com.example.cashmate.group.chooseGroup;

public class GroupItemBudget {
    private String name;
    private int icon;
    public GroupItemBudget(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }
    public String getName() { return name; }
    public int getIcon() { return icon; }
}
