package com.storagemanagement.app.scanshelf.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ItemStack extends AbstractRecord<Shelf> {
    public final static String tableName = DBContract.ITEM_STACK;
    public final static String tableSQL = "create table ItemStack(uuid varchar(255) NOT NULL PRIMARY KEY, parent varchar(255) NOT NULL, name varchar(255) NOT NULL, class varchar(255), quantity INT NOT NULL DEFAULT 0);";

    private int quantity;

    protected ItemStack(Manager manager, String uuid, String parent, String name) {
        super(tableName, manager, uuid, parent, name);
        this.quantity = 0;
    }

    protected ItemStack(Manager manager, ResultSet resultSet) throws SQLException {
        super(tableName, manager, resultSet);
        this.quantity = resultSet.getInt("quantity");
    }

    public int getQuantity() {
        return this.quantity;
    }

    public boolean setQuantity(int newQuantity) {
        this.quantity = newQuantity;
        return manager.database.update(tableName, this.uuid, "quantity=" + newQuantity + "");
    }

    public static class Manager extends AbstractManager<ItemStack, Shelf> {
        public Manager(DBConnection database) {
            super(database);
        }

        public ItemStack insert(Shelf shelf, String name) {
            return insert(shelf.uuid, name);
        }

        public ItemStack insert(String parent, String name) {
            ItemStack itemStack = new ItemStack(this, UUID.randomUUID().toString(), parent, name);
            return database.insert(tableName, "uuid, parent, name","'" + itemStack.uuid + "', '" + parent + "', '" + name + "'") ? itemStack : null;
        }

        @Override
        public boolean delete(String uuid) {
            return this.database.delete(tableName, uuid);
        }

        @Override
        public ItemStack byUUID(String uuid) {
            ItemStack result = null;

            return database.select(
                    tableName, "uuid ='" + uuid + "'",
                    (resultSet) -> {
                        while (resultSet.next()) {
                            return new ItemStack(this, resultSet);
                        }

                        return null;
                    });
        }

        @Override
        public List<ItemStack> byName(String pattern) {
            final List<ItemStack> result = new LinkedList<>();

            database.select(tableName, "name LIKE '%" + pattern + "%'", (resultSet) -> {
                while (resultSet.next()) {
                    result.add(new ItemStack(this, resultSet));
                }

                return null;
            });

            return result;
        }

        @Override
        public Shelf parentOf(ItemStack itemStack) {
            return database.shelfs.byUUID(itemStack.parent);
        }
    }
}
