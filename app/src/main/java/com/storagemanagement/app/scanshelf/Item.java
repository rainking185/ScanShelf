package com.storagemanagement.app.scanshelf;

public class Item {
    private String mCategory;
    private String mName;
    private String mUnit;
    public Item(String category, String name, String unit){
        mCategory=category;
        mName = name;
        mUnit = unit;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getUnit() {
        return mUnit;
    }

    public String getName() {
        return mName;
    }
}
