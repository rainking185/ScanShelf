package com.storagemanagement.app.scanshelf.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ItemClass extends AbstractRecord<Category> {
    public final static String tableName = DBContract.ITEM_CLASS;
    public final static String tableSQL = "create table ItemClass(" +
            "uuid varchar(255) NOT NULL PRIMARY KEY," +
            "parent varchar(255) NOT NULL," +
            "name varchar(255) NOT NULL," +
            "category varchar(255) NOT NULL," +
            "unit varchar(255) NOT NULL," +
            "description varchar(255));";

    private String category;
    private String unit;
    private String description;

    protected ItemClass(Manager manager, String uuid, String parent, String name) {
        super(tableName, manager, uuid, parent, name);
    }

    protected ItemClass(Manager manager, ResultSet resultSet) throws SQLException {
        super(tableName, manager, resultSet);
    }

    public static class Manager extends AbstractManager<ItemClass, Category> {
        public Manager(DBConnection database) {
            super(database);
        }

        public ItemClass insert(String name) {
            return insert("", name);
        }

        public ItemClass insert(String parent, String name) {
            ItemClass itemClass = new ItemClass(this, UUID.randomUUID().toString(), parent, name);
            return database.insert(tableName, "uuid, parent, name","'" + itemClass.uuid + "', '" + parent + "', '" + name + "'") ? itemClass : null;
        }

        @Override
        public boolean delete(String uuid) {
            return this.database.delete(tableName, uuid);
        }

        @Override
        public ItemClass byUUID(String uuid) {
            ItemClass result = null;

            return database.select(
                    tableName, "uuid ='" + uuid + "'",
                    (resultSet) -> {
                        while (resultSet.next()) {
                            return new ItemClass(this, resultSet);
                        }

                        return null;
                    });
        }

        @Override
        public List<ItemClass> byName(String pattern) {
            final List<ItemClass> result = new LinkedList<>();

            database.select(tableName, "name LIKE '%" + pattern + "%'", (resultSet) -> {
                while (resultSet.next()) {
                    result.add(new ItemClass(this, resultSet));
                }

                return null;
            });

            return result;
        }

        @Override
        public Category parentOf(ItemClass itemClass) {
            return database.categories.byUUID(itemClass.parent);
        }
    }
}
