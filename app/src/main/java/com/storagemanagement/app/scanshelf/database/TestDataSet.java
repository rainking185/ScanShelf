package com.storagemanagement.app.scanshelf.database;

import java.io.File;

public class TestDataSet {
    public static void init() {
        File dbFile = new File(DBConnection.defaultDBFileName);
        if (dbFile.isFile())
            dbFile.delete();

        DBConnection dbConnection = DBConnection.local();
        dbConnection.createTables();

        dbConnection.shelfs.insert("shelf1");
        Shelf shelfDiode = dbConnection.shelfs.insert("diodes");
        dbConnection.shelfs.insert("shelf2");
        dbConnection.shelfs.insert("transistors");

        ItemStack itemStack1N4148 = dbConnection.itemStacks.insert(shelfDiode,"1n4148");
        itemStack1N4148.setQuantity(10);
        itemStack1N4148.setParent(shelfDiode.uuid);

        ItemStack itemStack1N5819 = dbConnection.itemStacks.insert(shelfDiode,"1n5819");
        itemStack1N5819.setQuantity(20);
        itemStack1N5819.setParent(shelfDiode.uuid);

        dbConnection.close();
    }
}
