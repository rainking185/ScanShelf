package com.storagemanagement.app.scanshelf;

public class Location {
    private String mShelfName;
    private int mRow;
    private int mColumn;
    public Location(String shelfName, int row, int column){
        mShelfName=shelfName;
        mRow = row;
        mColumn = column;
    }

    public String getmShelfName() {
        return mShelfName;
    }

    public int getmColumn() {
        return mColumn;
    }

    public int getmRow() {
        return mRow;
    }
}
