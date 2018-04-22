package com.storagemanagement.app.scanshelf.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Category extends AbstractRecord<Category> {
    public final static String tableName = DBContract.CATEGORY;
    public final static String tableSQL = "create table Category(uuid varchar(255) NOT NULL PRIMARY KEY, parent varchar(255) NOT NULL, name varchar(255) NOT NULL);";

    protected Category(Manager manager, String uuid, String parent, String name) {
        super(tableName, manager, uuid, parent, name);
    }

    protected Category(Manager manager, ResultSet resultSet) throws SQLException {
        super(tableName, manager, resultSet);
    }

    public static class Manager extends AbstractManager<Category, Category> {
        public Manager(DBConnection database) {
            super(database);
        }

        public Category insert(String name) {
            return insert("", name);
        }

        public Category insert(String parent, String name) {
            Category category = new Category(this, UUID.randomUUID().toString(), parent, name);
            return database.insert(tableName, "uuid, parent, name","'" + category.uuid + "', '" + parent + "', '" + name + "'") ? category : null;
        }

        @Override
        public boolean delete(String uuid) {
            return this.database.delete(tableName, uuid);
        }

        @Override
        public Category byUUID(String uuid) {
            Category result = null;

            return database.select(
                    tableName, "uuid ='" + uuid + "'",
                    (resultSet) -> {
                        while (resultSet.next()) {
                            return new Category(this, resultSet);
                        }

                        return null;
                    });
        }

        @Override
        public List<Category> byName(String pattern) {
            final List<Category> result = new LinkedList<>();

            database.select(tableName, "name LIKE '%" + pattern + "%'", (resultSet) -> {
                while (resultSet.next()) {
                    result.add(new Category(this, resultSet));
                }

                return null;
            });

            return result;
        }

        @Override
        public Category parentOf(Category category) {
            return byUUID(category.parent);
        }
    }
}
