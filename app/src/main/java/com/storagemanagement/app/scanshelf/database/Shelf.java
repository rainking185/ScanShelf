package com.storagemanagement.app.scanshelf.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Shelf extends AbstractRecord<Shelf> {
    public final static String tableName = DBContract.SHELF;
    public final static String tableSQL = "create table Shelf(uuid varchar(255) NOT NULL PRIMARY KEY, parent varchar(255) NOT NULL, name varchar(255) NOT NULL);";

    protected Shelf(Manager manager, String uuid, String parent, String name) {
        super(tableName, manager, uuid, parent, name);
    }

    protected Shelf(Manager manager, ResultSet resultSet) throws SQLException {
        super(tableName, manager, resultSet);
    }

    public static class Manager extends AbstractManager<Shelf, Shelf> {
        public Manager(DBConnection database) {
            super(database);
        }

        public Shelf insert(String name) {
            return insert("", name);
        }

        public Shelf insert(String parent, String name) {
            Shelf shelf = new Shelf(this, UUID.randomUUID().toString(), parent, name);
            return database.insert(tableName, "uuid, parent, name","'" + shelf.uuid + "', '" + parent + "', '" + name + "'") ? shelf : null;
        }

        @Override
        public boolean delete(String uuid) {
            return this.database.delete(tableName, uuid);
        }

        @Override
        public Shelf byUUID(String uuid) {
            Shelf result = null;

            return database.select(
                    tableName, "uuid ='" + uuid + "'",
                    (resultSet) -> {
                while (resultSet.next()) {
                    return new Shelf(this, resultSet);
                }

                return null;
            });
        }

        @Override
        public List<Shelf> byName(String pattern) {
            final List<Shelf> result = new LinkedList<>();

            database.select(tableName, "name LIKE '%" + pattern + "%'", (resultSet) -> {
                while (resultSet.next()) {
                    result.add(new Shelf(this, resultSet));
                }

                return null;
            });

            return result;
        }

        @Override
        public Shelf parentOf(Shelf shelf) {
            return byUUID(shelf.parent);
        }
    }
}
