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

    public String getShelfName() {
        return mShelfName;
    }

    public int getColumn() {
        return mColumn;
    }

    public int getRow() {
        return mRow;
    }
}
