package com.storagemanagement.app.scanshelf.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractRecord<P extends AbstractRecord> {
    protected final String tableName;
    protected final AbstractManager manager;
    protected final String uuid;
    protected String parent;
    protected String name;

    protected AbstractRecord(String tableName, AbstractManager manager, String uuid, String parent, String name) {
        this.tableName = tableName;
        this.manager = manager;
        this.uuid = uuid;
        this.parent = parent;
        this.name = name;
    }

    protected AbstractRecord(String tableName, AbstractManager manager, ResultSet resultSet) throws SQLException {
        this.tableName = tableName;
        this.manager = manager;
        this.uuid = resultSet.getString("uuid");
        this.parent = resultSet.getString("parent");
        this.name = resultSet.getString("name");
    }

    public String getUuid() {
        return this.uuid;
    }

    public boolean remove() {
        return manager.database.delete(tableName, this.uuid);
    }

    public String getParentUUID() {
        return this.parent;
    }

    public boolean setParent(String newParent) {
        this.parent = newParent;
        return manager.database.update(tableName, this.uuid, "parent='" + newParent + "'");
    }

    public String getName() {
        return this.name;
    }

    public boolean setName(String newName) {
        this.name = newName;
        return manager.database.update(tableName, this.uuid, "name='" + newName + "'");
    }

    public P getParent() {
        return (P) manager.parentOf(this);
    }
}
