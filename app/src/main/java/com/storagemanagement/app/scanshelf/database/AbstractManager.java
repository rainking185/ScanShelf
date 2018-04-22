package com.storagemanagement.app.scanshelf.database;

import java.util.List;

public abstract class AbstractManager<T extends AbstractRecord, P extends AbstractRecord> {
    public final DBConnection database;
    public AbstractManager(DBConnection database) {
        this.database = database;
    }

    public abstract  T byUUID(String uuid);
    public abstract  List<T> byName(String pattern);
    public List<T> items() {
        return byName("");
    }
    public abstract  P parentOf(T itemStack);
    public abstract  boolean delete(String uuid);
}
