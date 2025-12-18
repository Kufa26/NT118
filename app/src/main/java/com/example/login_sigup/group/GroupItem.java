package com.example.login_sigup.group;

public class GroupItem {
    private String name;
    private int icon;
    public GroupItem(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }
    public String getName() { return name; }
    public int getIcon() { return icon; }
}
